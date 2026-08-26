package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ClientUtility;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AbyssalTroveBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {

    private int timeItemPlaced;
    public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5);
    private static final Vector3f itemRestingRotation = new Vector3f(0, 0, 0);
    private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
    public Vector3d playerPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
    public float degreesTowardsPlayer = 0;
    public float currentDisplayRotation = 0;
    private Set<PpIngredient> storedIngredients;

    public RendererData rendererData = new RendererData();

    public static class RendererData {
        public RendererData() {
            this.renderedItemTiers = new HashMap<>();
            this.state = State.HIDDEN;
            this.lastStateEntryTimes = new HashMap<>();
        }

        public enum State {
            HIDDEN,
            ALL_INGREDIENTS,
            ALL_LABELED_INGREDIENTS,
            ONLY_COMMON_INGREDIENTS,
            ONLY_RARE_INGREDIENTS,
            ONLY_DURATION_UPGRADES,
            ONLY_AMPLIFICATION_UPGRADES
        }

        public static class AbyssalTroveRenderedItem {
            public ItemStack icon;
            public List<ItemStack> subIcon;
            public Vector3d position;
            public PotionUpgradeIngredients.Rarity rarity;
            public float scale;
            public float subIconScale;

            public AbyssalTroveRenderedItem(ItemStack icon, List<ItemStack> subIcon, Vector3d position, PotionUpgradeIngredients.Rarity rarity) {
                this.icon = icon;
                this.subIcon = subIcon;
                this.position = position;
                this.rarity = rarity;
                this.scale = 0;
                this.subIconScale = 0;
            }

            public AbyssalTroveRenderedItem(ItemStack icon, List<ItemStack> subIcon, PotionUpgradeIngredients.Rarity rarity) {
                this(icon, subIcon, new Vector3d(0, 0, 0), rarity);
            }
        }

        public State getState() {
            return state;
        }

        public void setState(State newState) {
            state = newState;
            lastStateEntryTimes.put(state, (int) ClientTickHandler.total());
        }

        public void hide() {
            state = State.HIDDEN;
            lastStateEntryTimes.put(state, (int) ClientTickHandler.total());
        }

        public Map<Integer, List<AbyssalTroveRenderedItem>> renderedItemTiers;
        private State state;
        public Map<State, Integer> lastStateEntryTimes;
    }

    public AbyssalTroveBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.value(), pos, state);

        storedIngredients = new HashSet<>();
    }

    @Override
    protected int getSlots() {
        return 100;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssalTroveBlockEntity blockEntity) {
        if (level.isClientSide()) {
            Player player = ClientUtility.getLocalPlayer();
            if (player != null) {
                Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
                playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(pos.getX(), pos.getY(), pos.getZ());
                blockEntity.playerPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
                blockEntity.degreesTowardsPlayer = (float) Math.toDegrees(Math.atan2(playerPosRelativeToBlockOrigin.z - 0.5, playerPosRelativeToBlockOrigin.x - 0.5));

            } else {
                blockEntity.playerPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
                blockEntity.degreesTowardsPlayer = 0;
            }

            int lastStateChangeTime = blockEntity.getTimeLastStateChange(blockEntity.rendererData.getState());
            if (lastStateChangeTime > 0 && ClientTickHandler.total() - lastStateChangeTime > 300) { // 15 second timeout
                blockEntity.rendererData.hide();
            }
        }
    }

    @Override
    public int getTimeItemPlaced() {
        return timeItemPlaced;
    }

    public int getTimeLastStateChange(RendererData.State state) {
        return rendererData.lastStateEntryTimes.getOrDefault(state, -1);
    }

    @Override
    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(itemAnimationStartingPosRelativeToBlockOrigin.x, itemAnimationStartingPosRelativeToBlockOrigin.y, itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return itemRestingPositionTranslation;
    }

    @Override
    public Vector3f getRestingRotation() {
        return itemRestingRotation;
    }

    @Override
    public int getInputAnimationDuration() {
        return 20;
    }

    public void showGui() {
        if (rendererData.state == RendererData.State.HIDDEN) {
            timeItemPlaced = (int) ClientTickHandler.total();
        }

        // Skip states that would display nothing (e.g. "only rares" when none are known) - landing on an
        // empty display looks identical to HIDDEN but still consumes a cycle, which is confusing. HIDDEN
        // itself is never skipped, so this always terminates within one full loop of the state cycle.
        RendererData.State[] states = RendererData.State.values();
        RendererData.State next = rendererData.state;
        for (int i = 0; i < states.length; i++) {
            next = states[(next.ordinal() + 1) % states.length];
            if (next == RendererData.State.HIDDEN || stateHasVisibleItems(next)) {
                break;
            }
        }
        rendererData.setState(next);
    }

    private boolean stateHasVisibleItems(RendererData.State state) {
        for (List<RendererData.AbyssalTroveRenderedItem> items : rendererData.renderedItemTiers.values()) {
            for (RendererData.AbyssalTroveRenderedItem item : items) {
                boolean isUnknownIngredient = item.icon.is(DynamicIconItems.GENERIC_ICON.getValue());
                if (isIngredientVisibleInState(state, isUnknownIngredient, item.icon)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Whether an ingredient icon would be shown in the given display state. Shared between the block
     * entity (to skip cycling into an empty state) and the renderer (to decide per-item scale).
     */
    public static boolean isIngredientVisibleInState(RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        return switch (state) {
            case HIDDEN -> false;
            case ALL_INGREDIENTS, ALL_LABELED_INGREDIENTS -> true;
            case ONLY_COMMON_INGREDIENTS ->
                    !isUnknownIngredient && SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, PpIngredient.of(icon));
            case ONLY_RARE_INGREDIENTS ->
                    !isUnknownIngredient && SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, PpIngredient.of(icon));
            case ONLY_DURATION_UPGRADES ->
                    !isUnknownIngredient && Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon));
            case ONLY_AMPLIFICATION_UPGRADES ->
                    !isUnknownIngredient && Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon));
        };
    }

    public void updateRendererData() {
        RendererData data = new RendererData();

        if (Recipes.seededPotionRecipes == null) {
            return;
        }

        this.computeStoredIngredients();

        // Add items in tiers - adapted to square display in v1.3.0
        data.renderedItemTiers = new HashMap<>();
        int index = 0;
        Set<PpIngredient> allIngredients = getAcceptedIngredients();
        int sideLength = (int) Math.max(Math.round(Math.sqrt(allIngredients.size())), 1);
        for (PpIngredient ingredient : allIngredients) {
            int rowIndex = index / sideLength;
            List<RendererData.AbyssalTroveRenderedItem> itemsInRow = data.renderedItemTiers.computeIfAbsent(rowIndex, k -> new ArrayList<>());

            ItemStack icon = ingredient.getItemStack();
            List<ItemStack> subIcon = new ArrayList<>();
            if (!Debug.shouldRevealAllRecipes && !this.storedIngredients.contains(ingredient)) {
                icon = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.UNKNOWN_TEX_LOC);
            } else {
                if (Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    ItemStack sub = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.DUR_TEX_LOC);
                    subIcon.add(sub);
                }
                if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    ItemStack sub = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.AMP_TEX_LOC);
                    subIcon.add(sub);
                }
                if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, ingredient)) {
                    ItemStack sub = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.COMMON_TEX_LOC);
                    subIcon.add(sub);
                }
                if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, ingredient)) {
                    ItemStack sub = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.RARE_TEX_LOC);
                    subIcon.add(sub);
                }
            }

            itemsInRow.add(new RendererData.AbyssalTroveRenderedItem(icon, subIcon, PUtil.getRarity(ingredient)));

            index++;
        }

        final double tierSpacing = 1;
        final double itemSpacing = 1;

        // Set positions
        for (Map.Entry<Integer, List<RendererData.AbyssalTroveRenderedItem>> entry : data.renderedItemTiers.entrySet()) {
            List<RendererData.AbyssalTroveRenderedItem> items = entry.getValue();
            int tier = entry.getKey();
            for (int indexInRow = 0; indexInRow < items.size(); indexInRow++) {
                RendererData.AbyssalTroveRenderedItem item = items.get(indexInRow);
                item.position = new Vector3d(indexInRow * itemSpacing, tier * tierSpacing, 0);
            }
        }

        // Center items
        for (Map.Entry<Integer, List<RendererData.AbyssalTroveRenderedItem>> entry : data.renderedItemTiers.entrySet()) {
            List<RendererData.AbyssalTroveRenderedItem> items = entry.getValue();
            for (int indexInTier = 0; indexInTier < items.size(); indexInTier++) {
                RendererData.AbyssalTroveRenderedItem item = items.get(indexInTier);
                item.position.x = item.position.x - (items.size() * itemSpacing / 2.0);
                item.position.x += itemSpacing / 2.0;
            }
        }

        this.rendererData = data;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // setChanged() (which normally drives this) isn't invoked by the load path, so the floating
        // item display and the "known ingredient" reveal state would otherwise stay empty after a
        // world reload even though the underlying inventory items loaded correctly.
        updateRendererData();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        boolean canPlace = super.canPlaceItem(index, stack);
        return canPlace && isItemPotionIngredient(stack);
    }

    public static boolean isItemPotionIngredient(ItemStack stack) {
        return Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.isIngredientUsed(PpIngredient.of(stack));
    }

    public static Set<PpIngredient> ABYSSAL_TROVE_INGREDIENTS = new HashSet<>();

    public static Set<PpIngredient> getAcceptedIngredients() {
        return ABYSSAL_TROVE_INGREDIENTS;
    }

    public static void computeAbyssalTroveIngredients() {
        ABYSSAL_TROVE_INGREDIENTS = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getAllPotionBrewingIngredientsNoPotions().stream().sorted((a, b) -> {
            Function<PpIngredient, Integer> value = (ingredient) -> {
                if (Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    return 0;
                } else if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    return 1;
                } else {
                    return PUtil.getRarity(ingredient).ordinal() + 2;
                }
            };

            int comparison = Integer.compare(value.apply(a), value.apply(b));
            if (comparison != 0) {
                return comparison;
            }
            return a.getItemStack().getDisplayName().getString().compareTo(b.getItemStack().getDisplayName().getString());
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void setChanged() {
        super.setChanged();

        this.updateRendererData();
    }

    private void computeStoredIngredients() {
        storedIngredients.clear();
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                storedIngredients.add(PpIngredient.of(stack));
            }
        }
    }

    public Set<PpIngredient> getStoredIngredients() {
        return storedIngredients;
    }

    public float getFillPercentage() {
        return (float) storedIngredients.size() / getAcceptedIngredients().size();
    }
}

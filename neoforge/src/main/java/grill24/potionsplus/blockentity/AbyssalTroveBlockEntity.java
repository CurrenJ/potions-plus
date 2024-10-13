package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.*;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ClientUtility;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;

public class AbyssalTroveBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {

    private int timeItemPlaced;
    private int timeRarityIconsShown;
    public int extendHideDelayBy = 0;
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
        }

        public static class AbyssalTroveRenderedItem{
            public ItemStack icon;
            public List<ItemStack> subIcon;
            public Vector3d position;
            public PotionUpgradeIngredients.Rarity rarity;

            public AbyssalTroveRenderedItem(ItemStack icon, List<ItemStack> subIcon, Vector3d position, PotionUpgradeIngredients.Rarity rarity) {
                this.icon = icon;
                this.subIcon = subIcon;
                this.position = position;
                this.rarity = rarity;
            }

            public AbyssalTroveRenderedItem(ItemStack icon, List<ItemStack> subIcon, PotionUpgradeIngredients.Rarity rarity) {
                this(icon, subIcon, new Vector3d(0, 0, 0), rarity);
            }
        }

        public Map<Integer, List<AbyssalTroveRenderedItem>> renderedItemTiers;
    }

    public AbyssalTroveBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), pos, state);

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
        if (level.isClientSide) {
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
        }
    }

    @Override
    public int getTimeItemPlaced() {
        return timeItemPlaced;
    }

    public int getTimeRarityIconsShown() {
        return timeRarityIconsShown;
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
        if(timeRarityIconsShown < timeItemPlaced) {
            timeRarityIconsShown = (int) ClientTickHandler.total();
            extendHideDelayBy = timeRarityIconsShown > timeItemPlaced ? timeRarityIconsShown - timeItemPlaced : 0;
        } else {
            timeItemPlaced = (int) ClientTickHandler.total();
        }
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
        List<PpIngredient> allIngredients = Recipes.ALL_BCR_RECIPES_ANALYSIS.getAllPotionBrewingIngredientsNoPotions().stream().sorted((a, b) -> {
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
        }).toList();
        int sideLength = (int) Math.max(Math.round(Math.sqrt(allIngredients.size())), 1);
        for(PpIngredient ingredient : allIngredients) {
            int rowIndex = index / sideLength;
            List<RendererData.AbyssalTroveRenderedItem> itemsInRow = data.renderedItemTiers.computeIfAbsent(rowIndex, k -> new ArrayList<>());

            ItemStack icon = ingredient.getItemStack();
            List<ItemStack> subIcon = new ArrayList<>();
            if (!PotionsPlus.Debug.shouldRevealAllRecipes && !this.storedIngredients.contains(ingredient)) {
                icon = new ItemStack(Items.GENERIC_ICON.value(), 12);
            } else {
                if (Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    ItemStack sub = new ItemStack(Items.GENERIC_ICON.value());
                    sub.setCount(2);
                    subIcon.add(sub);
                }
                if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    ItemStack sub = new ItemStack(Items.GENERIC_ICON.value());
                    sub.setCount(1);
                    subIcon.add(sub);
                }
                if (SeededIngredientsLootTables.COMMON_INGREDIENTS_SET.get().contains(ingredient)) {
                    ItemStack sub = new ItemStack(Items.GENERIC_ICON.value());
                    sub.setCount(17);
                    subIcon.add(sub);
                }
                if (SeededIngredientsLootTables.RARE_INGREDIENTS_SET.get().contains(ingredient)) {
                    ItemStack sub = new ItemStack(Items.GENERIC_ICON.value());
                    sub.setCount(18);
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
    public boolean canPlaceItem(int index, ItemStack stack) {
        boolean canPlace = super.canPlaceItem(index, stack);
        boolean isIngredient = Recipes.ALL_BCR_RECIPES_ANALYSIS.isIngredientUsed(PpIngredient.of(stack));
        return canPlace && isIngredient;
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
}

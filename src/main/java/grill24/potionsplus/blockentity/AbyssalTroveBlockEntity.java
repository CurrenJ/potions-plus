package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.ClientCommands;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

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
        }

        public static class AbyssalTroveRenderedItem {
            public AbyssalTroveRenderedItem(ItemStack itemStack, Vector3d position, int tier) {
                this.itemStack = itemStack;
                this.position = position;
                this.tier = tier;
            }

            public AbyssalTroveRenderedItem(ItemStack itemStack, int tier) {
                this.itemStack = itemStack;
                this.position = new Vector3d(0, 0, 0);
                this.tier = tier;
            }

            public ItemStack itemStack;
            public Vector3d position;
            public int tier;
        }

        public Map<Integer, List<AbyssalTroveRenderedItem>> renderedItemTiers;
    }

    public AbyssalTroveBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.get(), pos, state);

        storedIngredients = new HashSet<>();
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new PotionsPlusContainer(100, 1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssalTroveBlockEntity blockEntity) {
        Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
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

    @Override
    public int getTimeItemPlaced() {
        return timeItemPlaced;
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

    public void onPlayerInsertItem(Player player) {
//        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
//        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
//        itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        timeItemPlaced = ((int) ClientTickHandler.total());
    }

    public void onRightClick(Player player) {
        timeItemPlaced = ((int) ClientTickHandler.total());
    }

    public void updateRendererData() {
        RendererData data = new RendererData();

        if (Recipes.seededPotionRecipes == null) {
            return;
        }

        this.computeStoredIngredients();

        // Add items in tiers
        data.renderedItemTiers = new HashMap<>();
        Recipes.seededPotionRecipes.allPotionsBrewingIngredientsByTierNoPotions.forEach((tier, ingredients) -> {
            if(tier >= 0) {
                List<RendererData.AbyssalTroveRenderedItem> itemsInTier = data.renderedItemTiers.computeIfAbsent(tier, k -> new ArrayList<>());

                ingredients.forEach(ingredient -> {
                    ItemStack stack = ingredient.getItemStack();
                    if (!ClientCommands.shouldRevealAllRecipes && !this.storedIngredients.contains(ingredient)) {
                        stack = new ItemStack(Items.GENERIC_ICON.get(), 12);
                    }

                    itemsInTier.add(new RendererData.AbyssalTroveRenderedItem(stack, tier));
                });
            }
        });

        final double tierSpacing = 1;
        final double itemSpacing = 0.5;

        // Set positions
        for (Map.Entry<Integer, List<RendererData.AbyssalTroveRenderedItem>> entry : data.renderedItemTiers.entrySet()) {
            List<RendererData.AbyssalTroveRenderedItem> items = entry.getValue();
            int tier = entry.getKey();
            for (int indexInTier = 0; indexInTier < items.size(); indexInTier++) {
                RendererData.AbyssalTroveRenderedItem item = items.get(indexInTier);
                item.position = new Vector3d(indexInTier * itemSpacing, tier * tierSpacing, 0);
            }
        }

        // Center items
        for (Map.Entry<Integer, List<RendererData.AbyssalTroveRenderedItem>> entry : data.renderedItemTiers.entrySet()) {
            List<RendererData.AbyssalTroveRenderedItem> items = entry.getValue();
            for (int indexInTier = 0; indexInTier < items.size(); indexInTier++) {
                RendererData.AbyssalTroveRenderedItem item = items.get(indexInTier);
                item.position.x = item.position.x - (items.size() * itemSpacing / 2.0);
            }
        }

        this.rendererData = data;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        boolean canPlace = super.canPlaceItem(index, stack);
        boolean isIngredient = Recipes.seededPotionRecipes.allPotionBrewingIngredientsNoPotions.contains(PpIngredient.of(stack));
        return canPlace && isIngredient;
    }

    @Override
    public void setChanged() {
        super.setChanged();

        this.updateRendererData();
    }

    private void computeStoredIngredients() {
        storedIngredients.clear();
        for (int i = 0; i < this.getItemHandler().getContainerSize(); i++) {
            ItemStack stack = this.getItemHandler().getItem(i);
            if (!stack.isEmpty()) {
                storedIngredients.add(PpIngredient.of(stack));
            }
        }
    }

    public Set<PpIngredient> getStoredIngredients() {
        return storedIngredients;
    }
}

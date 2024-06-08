package grill24.potionsplus.blockentity;

import com.google.common.primitives.Booleans;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import grill24.potionsplus.core.*;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HerbalistsLecternBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {

    public RendererData rendererData = new RendererData();
    private SoundInstance appearSound;
    private SoundInstance disappearSound;

    public static class RendererData {

        private int timeItemPlaced;
        public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5 - (1 / 8.0));
        private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
        public Vector3d nearbyPlayer = new Vector3d(0, 0, 0);
        private ItemStack[] itemStacksToDisplay;
        public boolean[] isAmpUpgrade;
        public boolean[] isDurationUpgrade;
        int ingredientTier = -1;
        Quaternion ingredientTierNumeralsRotation = Vector3f.XP.rotationDegrees(0);

        public RendererData() {
            itemStacksToDisplay = new ItemStack[0];
        }

        public void updateItemStacksToDisplay(HerbalistsLecternBlockEntity herbalistsLecternBlockEntity) {
            if (herbalistsLecternBlockEntity.level != null) {
                ItemStack inputStack = herbalistsLecternBlockEntity.getItemHandler().getItem(0);
                List<BrewingCauldronRecipe> allValidRecipes = new ArrayList<>(herbalistsLecternBlockEntity.level.getRecipeManager().getAllRecipesFor(
                                Recipes.BREWING_CAULDRON_RECIPE.get()).stream()
                        .filter(recipe -> recipe.isIngredient(inputStack)).toList());
                List<ItemStack> allShownItems = new ArrayList<>(allValidRecipes.stream().map(BrewingCauldronRecipe::getResultItem).toList());

                Set<MobEffect> uniquePotionTypes = new HashSet<>();
                List<ItemStack> itemsToRemove = new ArrayList<>();
                List<ItemStack> potionIconItemsToAdd = new ArrayList<>();
                List<Boolean> isAmpUpgrade = new ArrayList<>();
                List<Boolean> isDurationUpgrade = new ArrayList<>();

                for (BrewingCauldronRecipe recipe : allValidRecipes) {
                    ItemStack outputStack = recipe.getResultItem();

                    if (PUtil.isPotion(outputStack)) {
                        Potion outputPotion = PotionUtils.getPotion(outputStack);
                        if (HIDDEN_POTIONS.contains(outputPotion)) {
                            itemsToRemove.add(outputStack);
                        }

                        List<MobEffectInstance> mobEffects = PotionUtils.getPotion(outputStack).getEffects();
                        if (!mobEffects.isEmpty()) {
                            MobEffect mobEffectType = mobEffects.get(0).getEffect();
                            if (!uniquePotionTypes.contains(mobEffectType) && MobEffects.POTION_ICON_INDEX_MAP.get().containsKey(mobEffectType.getRegistryName())) {
                                uniquePotionTypes.add(mobEffectType);
                                isAmpUpgrade.add(false);
                                isDurationUpgrade.add(false);
                                potionIconItemsToAdd.add(new ItemStack(Items.POTION_EFFECT_ICON.get(), MobEffects.POTION_ICON_INDEX_MAP.get().get(mobEffectType.getRegistryName())));
                            }

                            if (uniquePotionTypes.contains(mobEffectType)) {
                                if (recipe.isAmpUpgrade()) {
                                    isAmpUpgrade.set(potionIconItemsToAdd.size() - 1, true);
                                }
                                if (recipe.isDurationUpgrade()) {
                                    isDurationUpgrade.set(potionIconItemsToAdd.size() - 1, true);
                                }
                                itemsToRemove.add(outputStack);
                            }
                        }
                    }
                }

                // Get max tier of all valid recipes, we display this
                // Check our modded ones first. If none, check vanilla
                // Bc turtle master potion is confusing in our display otherwise
                ingredientTier = -1;
                allValidRecipes.stream().filter(BrewingCauldronRecipe::isPotionsPlusPotion).map(BrewingCauldronRecipe::getOutputTier).max(Integer::compareTo).ifPresent(tier -> ingredientTier = tier);
                if (ingredientTier < 0) {
                    allValidRecipes.stream().map(BrewingCauldronRecipe::getOutputTier).max(Integer::compareTo).ifPresent(tier -> ingredientTier = tier);
                }

                allShownItems.removeAll(itemsToRemove);

                isAmpUpgrade.addAll(0, allShownItems.stream().map(stack -> false).toList());
                isDurationUpgrade.addAll(0, allShownItems.stream().map(stack -> false).toList());
                allShownItems.addAll(potionIconItemsToAdd);

                itemStacksToDisplay = allShownItems.toArray(new ItemStack[0]);
                this.isAmpUpgrade = Booleans.toArray(isAmpUpgrade);
                this.isDurationUpgrade = Booleans.toArray(isDurationUpgrade);
            } else {
                itemStacksToDisplay = new ItemStack[0];
                isAmpUpgrade = new boolean[0];
                isDurationUpgrade = new boolean[0];
            }


        }
    }

    public HerbalistsLecternBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.HERBALISTS_LECTERN_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new SimpleContainer(1);
    }

    public int getTimeItemPlaced() {
        return rendererData.timeItemPlaced;
    }

    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(rendererData.itemAnimationStartingPosRelativeToBlockOrigin.x, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.y, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return HerbalistsLecternBlockEntity.RendererData.itemRestingPositionTranslation;
    }

    @Override
    public int getInputAnimationDuration() {
        return 20;
    }

    public Vector3f getNearbyPlayer() {
        return new Vector3f((float) rendererData.nearbyPlayer.x, (float) rendererData.nearbyPlayer.y, (float) rendererData.nearbyPlayer.z);
    }

    public ItemStack[] getItemStacksToDisplay() {
        return rendererData.itemStacksToDisplay;
    }


    private static final Set<Potion> HIDDEN_POTIONS = Set.of(
            Potions.THICK,
            Potions.MUNDANE
    );

    public void onPlayerInsertItem(Player player) {
        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        rendererData.itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        rendererData.timeItemPlaced = ((int) ClientTickHandler.total());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HerbalistsLecternBlockEntity blockEntity) {
        Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
        if (player != null) {
            Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
            playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(pos.getX(), pos.getY(), pos.getZ());
            blockEntity.rendererData.nearbyPlayer = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);

            blockEntity.spawnParticlesIfPlayerIsHoldingIngredient(player, pos);
        } else {
            blockEntity.rendererData.nearbyPlayer = new Vector3d(0, 0, 0);
        }
    }

    private void spawnParticlesIfPlayerIsHoldingIngredient(Player player, BlockPos pos) {
        ItemStack heldItem = player.getMainHandItem();
        if (Recipes.seededPotionRecipes.allUniqueRecipeInputs != null && level != null && !heldItem.isEmpty()) {
            boolean hasEligibleIngredient = Recipes.seededPotionRecipes.allUniqueRecipeInputs.contains(PpIngredient.of(player.getMainHandItem()));

            if (hasEligibleIngredient) {
                if (level.random.nextInt(12) == 0)
                    player.level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + level.getRandom().nextDouble(-0.5, 0.5), pos.getY() + 1.25, pos.getZ() + 0.5 + level.getRandom().nextDouble(-0.5, 0.5), 0, 0, 0);
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        rendererData.updateItemStacksToDisplay(this);
    }

    public void playSoundAppear() {
        this.appearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_APPEAR.get(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), true);

        Utility.playSoundStopOther(this.appearSound, this.disappearSound);
    }

    public void playSoundDisappear() {
        this.disappearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_DISAPPEAR.get(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), true);

        Utility.playSoundStopOther(this.disappearSound, this.appearSound);
    }

}
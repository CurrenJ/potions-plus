package grill24.potionsplus.blockentity;

import com.google.common.primitives.Booleans;
import grill24.potionsplus.utility.ClientUtility;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
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

    // TODO: Implement properly client-sided sound playing here.
//    @OnlyIn(Dist.CLIENT)
//    private SoundInstance appearSound;
//    @OnlyIn(Dist.CLIENT)
//    private SoundInstance disappearSound;

    public static class RendererData {

        private int timeItemPlaced;
        public Vector3f rotation = new Vector3f(90, 0, 0);
        public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5);
        private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
        public Vector3d localPlayerPositionRelativeToBlockEntity = new Vector3d(0, 0, 0);
        private ItemStack[] itemStacksToDisplay;
        public boolean[] isAmpUpgrade;
        public boolean[] isDurationUpgrade;
        int ingredientTier = -1;
        Quaternionf ingredientTierNumeralsRotation = new Quaternionf().identity();

        public RendererData() {
            itemStacksToDisplay = new ItemStack[0];
        }

        public void updateItemStacksToDisplay(HerbalistsLecternBlockEntity herbalistsLecternBlockEntity) {
            if (herbalistsLecternBlockEntity.level != null) {
                ItemStack inputStack = herbalistsLecternBlockEntity.getItem(0);
                List<BrewingCauldronRecipe> allValidRecipes = new ArrayList<>(herbalistsLecternBlockEntity.level.getRecipeManager().getAllRecipesFor(
                                Recipes.BREWING_CAULDRON_RECIPE.value()).stream().map(RecipeHolder::value)
                        .filter(recipe -> recipe.isIngredient(inputStack)).toList());
                List<PpIngredient> allShownItems = new ArrayList<>(allValidRecipes.stream().map(recipe -> PpIngredient.of(recipe.getResultItem())).toList());

                Set<MobEffect> uniquePotionTypes = new HashSet<>();
                List<PpIngredient> itemsToRemove = new ArrayList<>();
                List<PpIngredient> potionIconItemsToAdd = new ArrayList<>();
                List<Boolean> isAmpUpgrade = new ArrayList<>();
                List<Boolean> isDurationUpgrade = new ArrayList<>();

                for (BrewingCauldronRecipe recipe : allValidRecipes) {
                    ItemStack outputStack = recipe.getResultItem();

                    if (PUtil.isPotion(outputStack)) {
                        Potion outputPotion = PUtil.getPotion(outputStack);
                        if (HIDDEN_POTIONS.contains(outputPotion)) {
                            itemsToRemove.add(PpIngredient.of(recipe.getResultItem()));
                        }

                        List<MobEffectInstance> mobEffects = PUtil.getPotion(outputStack).getEffects();
                        if (!mobEffects.isEmpty()) {
                            MobEffect mobEffectType = mobEffects.get(0).getEffect().value();
                            ResourceLocation mobEffectTypeRegistryName = BuiltInRegistries.MOB_EFFECT.getKey(mobEffectType);
                            if (!uniquePotionTypes.contains(mobEffectType) && MobEffects.POTION_ICON_INDEX_MAP.get().containsKey(mobEffectTypeRegistryName)) {
                                uniquePotionTypes.add(mobEffectType);
                                isAmpUpgrade.add(false);
                                isDurationUpgrade.add(false);
                                // TODO: Move all potion icon / generic icon to a separate class - encapsulate this itemstack creation as utility
                                potionIconItemsToAdd.add(PpIngredient.of(new ItemStack(Items.POTION_EFFECT_ICON.value(), MobEffects.POTION_ICON_INDEX_MAP.get().get(mobEffectTypeRegistryName))));
                            }

                            if (uniquePotionTypes.contains(mobEffectType)) {
                                if (recipe.isAmpUpgrade()) {
                                    isAmpUpgrade.set(potionIconItemsToAdd.size() - 1, true);
                                }
                                if (recipe.isDurationUpgrade()) {
                                    isDurationUpgrade.set(potionIconItemsToAdd.size() - 1, true);
                                }
                                itemsToRemove.add(PpIngredient.of(recipe.getResultItem()));
                            }
                        }
                    }
                }

                // Get max tier of all valid recipes, we display this
                // Check our modded ones first. If none, check vanilla
                // Bc turtle master potion is confusing in our display otherwise
                ingredientTier = -1;
                allValidRecipes.stream().map(BrewingCauldronRecipe::getOutputTier).max(Integer::compareTo).ifPresent(tier -> ingredientTier = tier);

                allShownItems.removeAll(itemsToRemove);

                isAmpUpgrade.addAll(0, allShownItems.stream().map(stack -> false).toList());
                isDurationUpgrade.addAll(0, allShownItems.stream().map(stack -> false).toList());
                allShownItems.addAll(potionIconItemsToAdd);

                itemStacksToDisplay = allShownItems.stream().map(PpIngredient::getItemStack).toArray(ItemStack[]::new);
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
    protected int getSlots() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public int getTimeItemPlaced() {
        return rendererData.timeItemPlaced;
    }

    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(rendererData.itemAnimationStartingPosRelativeToBlockOrigin.x, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.y, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return RendererData.itemRestingPositionTranslation;
    }

    @Override
    public Vector3f getRestingRotation() {
        return rendererData.rotation;
    }

    @Override
    public int getInputAnimationDuration() {
        return 20;
    }

    public Vector3f getLocalPlayerRelativePosition() {
        return new Vector3f((float) rendererData.localPlayerPositionRelativeToBlockEntity.x, (float) rendererData.localPlayerPositionRelativeToBlockEntity.y, (float) rendererData.localPlayerPositionRelativeToBlockEntity.z);
    }

    public ItemStack[] getItemStacksToDisplay() {
        return rendererData.itemStacksToDisplay;
    }


    private static final Set<Holder<Potion>> HIDDEN_POTIONS = Set.of(
            Potions.THICK,
            Potions.MUNDANE
    );

    public void onPlayerInsertItem(Player player) {
        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        rendererData.itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        rendererData.timeItemPlaced = ((int) ClientTickHandler.total());
        rendererData.rotation = new Vector3f(90, 0, Utility.getHorizontalDirectionTowardsBlock(player.blockPosition(), this.getBlockPos()).toYRot());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HerbalistsLecternBlockEntity blockEntity) {
        if(level.isClientSide) {
            Player player = ClientUtility.getLocalPlayer();
            if (player != null) {
                Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
                playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(pos.getX(), pos.getY(), pos.getZ());
                blockEntity.rendererData.localPlayerPositionRelativeToBlockEntity = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);

                blockEntity.spawnParticlesIfPlayerIsHoldingIngredient(player, pos);
            } else {
                blockEntity.rendererData.localPlayerPositionRelativeToBlockEntity = new Vector3d(0, 0, 0);
            }
        }
    }

    private void spawnParticlesIfPlayerIsHoldingIngredient(Player player, BlockPos pos) {
        ItemStack heldItem = player.getMainHandItem();
        if (Recipes.seededPotionRecipes.allUniqueRecipeInputs != null && level != null && !heldItem.isEmpty()) {
            boolean hasEligibleIngredient = Recipes.seededPotionRecipes.allUniqueRecipeInputs.contains(PpIngredient.of(player.getMainHandItem()));

            if (hasEligibleIngredient) {
                if (level.random.nextInt(12) == 0)
                    player.level().addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + level.getRandom().nextDouble() - 0.5, pos.getY() + 1.25, pos.getZ() + 0.5 + level.getRandom().nextDouble() - 0.5, 0, 0, 0);
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        rendererData.updateItemStacksToDisplay(this);
    }

    // TODO: Implement properly client-sided sound playing here.
/**
    public void playSoundAppear() {
        this.appearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_APPEAR.value(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), true);

        Utility.playSoundStopOther(this.appearSound, this.disappearSound);
    }

    public void playSoundDisappear() {
        this.disappearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_DISAPPEAR.value(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), true);

        Utility.playSoundStopOther(this.disappearSound, this.appearSound);
    }
 */

}
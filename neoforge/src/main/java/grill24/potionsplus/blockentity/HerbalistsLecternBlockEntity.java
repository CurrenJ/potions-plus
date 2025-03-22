package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.ClientUtility;
import net.minecraft.core.Holder;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HerbalistsLecternBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {

    public HerbalistsLecternSounds sounds;
    public RendererData rendererData = new RendererData();

    public static class RendererData {
        public record IconData(PpIngredient displayStack, List<PpIngredient> subIcons) {}

        private int timeItemPlaced;
        public Vector3f rotation = new Vector3f(90, 0, 0);
        public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5);
        private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
        public Vector3d localPlayerPositionRelativeToBlockEntity = new Vector3d(0, 0, 0);
        Quaternionf ingredientTierNumeralsRotation = new Quaternionf().identity();

        public List<IconData> allIcons = new ArrayList<>();
        ItemStack centerDisplayStack = ItemStack.EMPTY;

        public RendererData() {}

        public void updateItemStacksToDisplay(HerbalistsLecternBlockEntity herbalistsLecternBlockEntity) {
            if (herbalistsLecternBlockEntity.level != null) {
                ItemStack inputStack = herbalistsLecternBlockEntity.getItem(0);
                PpIngredient inputIngredient = PpIngredient.of(inputStack);

                List<RecipeHolder<BrewingCauldronRecipe>> recipesWithInputIngredient = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipesForIngredient(inputIngredient);
                Map<ResourceLocation, IconData> potionIcons = new HashMap<>();
                List<IconData> additionalIcons = new ArrayList<>();

                allIcons.clear();

                // Iterate all the recipes that the input ingredient is used in and collect the potion icons, as well as the sub-icons to display on each potion icon.
                for (RecipeHolder<BrewingCauldronRecipe> recipeHolder : recipesWithInputIngredient) {
                    BrewingCauldronRecipe recipe = recipeHolder.value();
                    ItemStack outputStack = recipe.getResult();
                    List<MobEffectInstance> outputEffects = PUtil.getAllEffects(outputStack);

                    // If the recipe has no potion effects on its output, just display the result item.
                    if (outputEffects.isEmpty()) {
                        additionalIcons.add(new IconData(PpIngredient.of(outputStack), new ArrayList<>()));
                    }
                    // If the potion has effects, display the potion icon. No duplicates.
                    for (MobEffectInstance mobEffectInstance : outputEffects) {
                        ResourceLocation mobEffectId = mobEffectInstance.getEffect().getKey().location();

                        boolean isMobEffectInIconDataAlready = potionIcons.containsKey(mobEffectId);
                        boolean doesIconExistForMobEffect = MobEffects.POTION_ICON_INDEX_MAP.get().containsKey(mobEffectId);
                        // If we haven't seen this MobEffect type yet and it has an icon, add it to the map of potion icons
                        if (!isMobEffectInIconDataAlready && doesIconExistForMobEffect) {
                            ItemStack displayStack = new ItemStack(Items.POTION_EFFECT_ICON.value(), 1);
                            displayStack.setCount(MobEffects.POTION_ICON_INDEX_MAP.get().get(mobEffectId));
                            potionIcons.put(mobEffectId, new IconData(PpIngredient.of(displayStack), new ArrayList<>()));

                            // Add the sub-icons to the potion icon.
                            List<PpIngredient> subIcons = potionIcons.get(mobEffectId).subIcons;
                            // Check if any ingredients are part of the "common" or "rare" ingredient sets.
                            // If so, add the appropriate sub-icon to the potion icon.
                            List<PpIngredient> recipeIngredients = recipe.getPpIngredients();
                            for (PpIngredient recipeIngredient : recipeIngredients) {
                                if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, recipeIngredient)) {
                                    ItemStack common = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.COMMON_TEX_LOC);
                                    subIcons.add(PpIngredient.of(common));
                                }
                                if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, recipeIngredient)) {
                                    ItemStack rare = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.RARE_TEX_LOC);
                                    subIcons.add(PpIngredient.of(rare));
                                }
                            }
                        }
                    }
                }
                // Add all the potion icons and additional icons to the list of all icons to display.
                allIcons.addAll(potionIcons.values());
                allIcons.addAll(additionalIcons);

                // Update the center display stacks - either common, rare, or N/A ingredient.
                PpIngredient ingredient = PpIngredient.of(inputStack);
                if(Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    this.centerDisplayStack = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.DUR_TEX_LOC);
                } else if (Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(ingredient)) {
                    this.centerDisplayStack = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.AMP_TEX_LOC);
                } else if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, ingredient)) {
                    this.centerDisplayStack = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.COMMON_TEX_LOC);
                } else if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, ingredient)) {
                    this.centerDisplayStack = Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.RARE_TEX_LOC);
                } else {
                    this.centerDisplayStack = ItemStack.EMPTY;
                }
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
        if (level != null && !heldItem.isEmpty()) {
            boolean hasEligibleIngredient = Recipes.ALL_BCR_RECIPES_ANALYSIS.isIngredientUsed(PpIngredient.of(heldItem));

            if (hasEligibleIngredient) {
                if (level.random.nextInt(12) == 0)
                    player.level().addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5 + level.getRandom().nextDouble() - 0.5, pos.getY() + 1.25, pos.getZ() + 0.5 + level.getRandom().nextDouble() - 0.5, 0, 0, 0);
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();

        // Pre-process the renderer data for the client so we don't have to do it every frame.
        if (level != null && level.isClientSide) {
            rendererData.updateItemStacksToDisplay(this);
        }
    }

}
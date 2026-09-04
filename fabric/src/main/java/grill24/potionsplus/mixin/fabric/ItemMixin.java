package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.core.RecipesRegistrar;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ItemStacksTooltip;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fabric equivalent of NeoForge's {@code ItemListenersMod} ({@code ModifyDefaultComponentsEvent}) -
 * bumps every {@link PotionItem}'s (including vanilla potions) default max stack size to 16. Fabric
 * has no such event and 1.21.1 has no {@code BuiltInRegistries.DATA_COMPONENT_INITIALIZERS} (that's
 * a 1.21.5+ API - verified absent via a jar listing against this module's vanilla dependency), so we
 * mixin into {@link Item}'s constructor, which is where vanilla bakes {@code components} once and
 * for all from {@link Item.Properties}.
 *
 * <p>Also mirrors NeoForge's {@code mixin.neoforge.ItemMixin#getTooltipImage} - without this override,
 * no {@link ItemStacksTooltip} is ever produced client-side, so the brewing-recipe item grid never
 * appears in tooltips on Fabric even though {@code event.fabric.ClientTooltipComponentFactoriesListeners}
 * is correctly wired to render one once it exists.
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    @Mutable
    @Shadow
    @Final
    private DataComponentMap components;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void potionsplus$bumpPotionStackSize(Item.Properties properties, CallbackInfo ci) {
        if ((Object) this instanceof PotionItem) {
            this.components = DataComponentMap.builder()
                    .addAll(this.components)
                    .set(DataComponents.MAX_STACK_SIZE, 16)
                    .build();
        }
    }

    @Inject(method = "getTooltipImage", at = @At("RETURN"), cancellable = true)
    private void potionsplus$getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        List<List<ItemStack>> displayStacks = new ArrayList<>();
        if (PotionContainer.isPotionStack(stack)) {
            List<MobEffectInstance> effects = PotionData.read(stack).effects();
            if (effects.size() == 1) {
                ResourceKey<MobEffect> mobEffect = effects.getFirst().getEffect().unwrapKey().orElseThrow();
                List<RecipeHolder<BrewingCauldronRecipe>> recipes = RecipesRegistrar.ALL_BCR_RECIPES_ANALYSIS.getRecipesForMobEffect(mobEffect);
                if (!recipes.isEmpty()) {
                    RecipeHolder<BrewingCauldronRecipe> recipe = recipes.getFirst();
                    displayStacks.add(potionsplus$createTooltipFromBrewingRecipe(recipe));
                }
            }
        } else {
            // ingredientToRecipeMap is keyed on count-1 ingredient stacks - normalize or a held stack
            // of >1 would never match and the recipe tooltip would silently never render.
            PpIngredient ingredient = PpIngredient.of(stack.copyWithCount(1));
            List<RecipeHolder<BrewingCauldronRecipe>> recipes = RecipesRegistrar.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipesForIngredient(ingredient);
            for (RecipeHolder<BrewingCauldronRecipe> recipe : recipes) {
                displayStacks.add(potionsplus$createTooltipFromBrewingRecipe(recipe));
            }
        }

        if (!displayStacks.isEmpty()) {
            cir.setReturnValue(Optional.of(ItemStacksTooltip.of(displayStacks, true, false)));
        }
    }

    @Unique
    private List<ItemStack> potionsplus$createTooltipFromBrewingRecipe(RecipeHolder<BrewingCauldronRecipe> recipe) {
        List<ItemStack> displayStacks = new ArrayList<>(recipe.value().getIngredientsAsItemStacks().stream().toList());
        displayStacks.addFirst(new ItemStack(BlockEntityBlocks.BREWING_CAULDRON.value()));
        return displayStacks;
    }
}

package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin implements FeatureElement, ItemLike, net.neoforged.neoforge.common.extensions.IItemExtension {
    @Inject(method = "getTooltipImage", at = @At("RETURN"), cancellable = true)
    private void getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        List<List<ItemStack>> displayStacks = new ArrayList<>();
        if (PUtil.isPotion(stack)) {
            List<MobEffectInstance> effects = PUtil.getAllEffects(stack);
            if (effects.size() == 1) {
                ResourceKey<MobEffect> mobEffect = effects.getFirst().getEffect().getKey();
                List<RecipeHolder<BrewingCauldronRecipe>> recipes = Recipes.ALL_BCR_RECIPES_ANALYSIS.getRecipesForMobEffect(mobEffect);
                if(!recipes.isEmpty()) {
                    RecipeHolder<BrewingCauldronRecipe> recipe = recipes.getFirst();
                    displayStacks.add(potions_plus$createTooltipFromBrewingRecipe(recipe));
                }
            }
        } else {
                PpIngredient ingredient = PpIngredient.of(stack);
                List<RecipeHolder<BrewingCauldronRecipe>> recipes = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getRecipesForIngredient(ingredient);
                for (RecipeHolder<BrewingCauldronRecipe> recipe : recipes) {
                    displayStacks.add(potions_plus$createTooltipFromBrewingRecipe(recipe));
                }
        }

        cir.setReturnValue(Optional.of(ItemStacksTooltip.of(displayStacks)));
    }

    @Unique
    private List<ItemStack> potions_plus$createTooltipFromBrewingRecipe(RecipeHolder<BrewingCauldronRecipe> recipe) {
        List<ItemStack> displayStacks = new ArrayList<>(recipe.value().getIngredientsAsItemStacks().stream().toList());
        displayStacks.addFirst(new ItemStack(Blocks.BREWING_CAULDRON.value()));
        return displayStacks;
    }
}

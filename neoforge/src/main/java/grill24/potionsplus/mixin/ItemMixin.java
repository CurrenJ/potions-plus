package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.skill.reward.OwnerDataComponent;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

        if (!displayStacks.isEmpty()) {
            cir.setReturnValue(Optional.of(ItemStacksTooltip.of(displayStacks)));
        }
    }

    /**
     * Creates a list of item stacks to display in the tooltip from a brewing recipe.
     * 'Hiding' unknown ingredients with the question mark is done in {@link grill24.potionsplus.utility.ClientItemStacksTooltip}
     * @param recipe the recipe
     * @return the list of item stacks - always starts with a brewing cauldron
     */
    @Unique
    private List<ItemStack> potions_plus$createTooltipFromBrewingRecipe(RecipeHolder<BrewingCauldronRecipe> recipe) {
        List<ItemStack> displayStacks = new ArrayList<>(recipe.value().getIngredientsAsItemStacks().stream().toList());
        displayStacks.addFirst(new ItemStack(Blocks.BREWING_CAULDRON.value()));
        return displayStacks;
    }

    /**
     * Redirects the canEat method to check if the player is the owner of the choice item.
     * See {@link grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent} and {@link OwnerDataComponent}
     * @param p the player
     * @param canAlwaysEat if the food can be eaten regardless of the player's hunger
     * @param level the level
     * @param player the player
     * @param hand the hand
     * @return true if item eating should be allowed, false otherwisea
     */
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"))
    private boolean potions_plus$canEat(Player p, boolean canAlwaysEat, Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.has(DataComponents.CHOICE_ITEM) && itemStack.has(DataComponents.OWNER)) {
            OwnerDataComponent ownerData = itemStack.get(DataComponents.OWNER);
            if (ownerData != null) {
                return ownerData.isOwner(player);
            }
        }
        return player.canEat(canAlwaysEat);
    }
}

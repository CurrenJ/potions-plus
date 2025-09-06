package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeCodecs {
    public static final Codec<RecipeHolder<?>> RECIPE_HOLDER_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceKey.codec(Registries.RECIPE).fieldOf("recipe").forGetter(RecipeHolder::id),
            Recipe.CODEC.fieldOf("value").forGetter(RecipeHolder::value)
    ).apply(builder, RecipeHolder::new));
    public static final Codec<BrewingCauldronRecipe> BREWING_CAULDRON_RECIPE_CODEC = Recipes.BREWING_CAULDRON_RECIPE_SERIALIZER.value().codec().codec();
    public static final Codec<RecipeHolder<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_HOLDER_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceKey.codec(Registries.RECIPE).fieldOf("recipe").forGetter(RecipeHolder::id),
            BREWING_CAULDRON_RECIPE_CODEC.fieldOf("value").forGetter(RecipeHolder::value)
    ).apply(builder, RecipeHolder::new));
}

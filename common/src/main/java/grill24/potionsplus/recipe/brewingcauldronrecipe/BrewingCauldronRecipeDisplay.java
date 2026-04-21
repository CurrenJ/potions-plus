package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Recipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record BrewingCauldronRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result,
                                           SlotDisplay craftingStation) implements RecipeDisplay {
    public static final MapCodec<BrewingCauldronRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(BrewingCauldronRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(BrewingCauldronRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(BrewingCauldronRecipeDisplay::craftingStation)
                    )
                    .apply(builder, BrewingCauldronRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
            BrewingCauldronRecipeDisplay::ingredients,
            SlotDisplay.STREAM_CODEC,
            BrewingCauldronRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            BrewingCauldronRecipeDisplay::craftingStation,
            BrewingCauldronRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<BrewingCauldronRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<? extends RecipeDisplay> type() {
        return Recipes.BREWING_CAULDRON_RECIPE_DISPLAY.value();
    }
}

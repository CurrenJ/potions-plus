package grill24.potionsplus.core.seededrecipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;

public class PpIngredient {
    public Ingredient[] ingredients;

    public static final MapCodec<PpIngredient> CODEC = RecordCodecBuilder.mapCodec(
            codecBuilder -> codecBuilder.group(
                            ItemStack.STRICT_CODEC.fieldOf("ppIngredient").forGetter(PpIngredient::getItemStack)
            ).apply(codecBuilder, PpIngredient::of)
    );
    public static final Codec<List<PpIngredient>> LIST_CODEC = CODEC.codec().listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, PpIngredient> STREAM_CODEC = StreamCodec.of(
            (byteBuf, ppIngredient) -> {
                ItemStack.STREAM_CODEC.encode(byteBuf, ppIngredient.getItemStack());
            },
            (byteBuf) -> {
                ItemStack itemStack = ItemStack.STREAM_CODEC.decode(byteBuf);
                return PpIngredient.of(itemStack);
            }
    );

    protected PpIngredient(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    // Hashcode and equals methods
    @Override
    public int hashCode() {
        StringBuilder result = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getItems()) {
                result.append(PUtil.getNameOrVerbosePotionName(stack));
            }
        }
        return result.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PpIngredient)) {
            return false;
        }
        PpIngredient other = (PpIngredient) obj;
        if (ingredients.length != other.ingredients.length) {
            return false;
        }
        for (int i = 0; i < ingredients.length; i++) {
            if (!toJsonOrThrow(ingredients[i]).equals(toJsonOrThrow(other.ingredients[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Ingredient ingredient : ingredients) {
            result.append(" ");
            result.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0]));
        }
        result.append(" ]");
        return result.toString();
    }

    public boolean contains(Ingredient ingredient) {
        for (Ingredient i : ingredients) {
            if (toJsonOrThrow(i).equals(toJsonOrThrow(ingredient))) {
                return true;
            }
        }
        return false;
    }

    private static JsonElement toJsonOrThrow(Ingredient ingredient) {
        DataResult<JsonElement> result = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient);
        return result.result().orElseThrow();
    }

    public ItemStack getItemStack() {
        assert ingredients.length == 1 : "Tried to get item stack, but there are multiple.";
        return ingredients[0].getItems()[0];
    }

    public static PpIngredient of(ItemStack... stacks) {
        return new PpIngredient(Arrays.stream(stacks).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    public static PpIngredient of(Ingredient... ingredients) {
        return new PpIngredient(ingredients);
    }
}

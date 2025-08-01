package grill24.potionsplus.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class ShapelessProcessingRecipeSerializerHelper {
    public static final Codec<RecipeCategory> RECIPE_CATEGORY_CODEC = Codec.STRING.comapFlatMap(
            (p_340780_) -> {
                Optional<RecipeCategory> optional;
                try {
                    optional = Optional.of(RecipeCategory.valueOf(p_340780_));
                } catch (IllegalArgumentException illegalargumentexception) {
                    optional = Optional.empty();
                }
                return optional.map(DataResult::success).orElseGet(() -> {
                    return DataResult.error(() -> "Unknown recipe category: " + p_340780_);
                });
            },
            RecipeCategory::toString
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeCategory> RECIPE_CATEGORY_STREAM_CODEC = StreamCodec.of(
            FriendlyByteBuf::writeEnum,
            (buffer) -> buffer.readEnum(RecipeCategory.class)
    );
}

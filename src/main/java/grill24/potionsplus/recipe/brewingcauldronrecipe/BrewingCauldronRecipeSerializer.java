package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.google.gson.JsonObject;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BrewingCauldronRecipeSerializer extends ShapelessProcessingRecipeSerializer<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {

    public BrewingCauldronRecipeSerializer(Supplier<BrewingCauldronRecipeBuilder> builderSupplier, int defaultProcessingTime) {
        super(builderSupplier, defaultProcessingTime);
    }

    public @NotNull BrewingCauldronRecipeBuilder fromJson(BrewingCauldronRecipeBuilder builder, @NotNull JsonObject jsonObject) {
        super.fromJson(builder, jsonObject);

        float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
        int tier = GsonHelper.getAsInt(jsonObject, "tier", -1);

        return builder.experience(experience).tier(tier);
    }

    @Override
    public void toJson(BrewingCauldronRecipe recipe, JsonObject jsonObject) {
        super.toJson(recipe, jsonObject);

        jsonObject.addProperty("experience", recipe.experience);
        jsonObject.addProperty("tier", recipe.tier);
    }

    @Override
    public BrewingCauldronRecipeBuilder fromNetwork(BrewingCauldronRecipeBuilder builder, FriendlyByteBuf buf) {
        super.fromNetwork(builder, buf);

        int tier = buf.readVarInt();
        float experience = buf.readFloat();

        return builder.tier(tier).experience(experience);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, BrewingCauldronRecipe obj) {
        super.toNetwork(buf, obj);

        buf.writeVarInt(obj.tier);
        buf.writeFloat(obj.experience);
    }
}


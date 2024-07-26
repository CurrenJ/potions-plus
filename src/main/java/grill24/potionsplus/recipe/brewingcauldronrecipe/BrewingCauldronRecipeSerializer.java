package grill24.potionsplus.recipe.brewingcauldronrecipe;

import com.google.gson.JsonObject;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public class BrewingCauldronRecipeSerializer extends ShapelessProcessingRecipeSerializer<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {

    public BrewingCauldronRecipeSerializer(int defaultProcessingTime) {
        super(defaultProcessingTime);
    }

    @Override
    public @NotNull BrewingCauldronRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject jsonObject) {
        BrewingCauldronRecipeBuilder builder = new BrewingCauldronRecipeBuilder();
        super.fromJson(builder, jsonObject);

        // Read experience and processing time
        float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
        // Read tier
        int tier = GsonHelper.getAsInt(jsonObject, "tier", -1);

        return builder.experience(experience).tier(tier).build(resourceLocation);
    }

    @Override
    public void toJson(BrewingCauldronRecipe recipe, JsonObject jsonObject) {
        super.toJson(recipe, jsonObject);
        jsonObject.addProperty("experience", recipe.experience);
        jsonObject.addProperty("tier", recipe.tier);
    }

    @Override
    public BrewingCauldronRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf buf) {
        BrewingCauldronRecipeBuilder builder = new BrewingCauldronRecipeBuilder();
        super.fromNetwork(builder, buf);

        // Read tier
        int tier = buf.readVarInt();
        // Read result, experience, and processing time
        float experience = buf.readFloat();

        // Create the recipe
        return builder.tier(tier).experience(experience).build(resourceLocation);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, BrewingCauldronRecipe obj) {
        super.toNetwork(buf, obj);

        // Write tier
        buf.writeVarInt(obj.tier);
        buf.writeFloat(obj.experience);
    }
}


//package grill24.potionsplus.recipe.brewingcauldronrecipe;
//
//import com.google.gson.JsonObject;
//import grill24.potionsplus.recipe.ShapelessProcessingRecipe;
//import grill24.potionsplus.recipe.ShapelessProcessingRecipeSerializerHelper;
//import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
//import net.minecraft.data.recipes.RecipeCategory;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.function.Supplier;
//
//public class BrewingCauldronRecipeSerializer extends ShapelessProcessingRecipeSerializerHelper<BrewingCauldronRecipe, BrewingCauldronRecipeBuilder> {
//    public static StreamCodec<RegistryFriendlyByteBuf, BrewingCauldronRecipe> STREAM_CODEC = StreamCodec.ofMember(
//            (recipe, buf) -> t
//            (buf) -> BrewingCauldronRecipe.fromNetwork(buf)
//    );
//
//    public BrewingCauldronRecipeSerializer(Supplier<BrewingCauldronRecipeBuilder> builderSupplier, int defaultProcessingTime) {
//        super(builderSupplier, defaultProcessingTime);
//    }
//
//    public @NotNull BrewingCauldronRecipeBuilder fromJson(BrewingCauldronRecipeBuilder builder, @NotNull JsonObject jsonObject) {
//        super.fromJson(builder, jsonObject);
//
//        float experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
//        int tier = GsonHelper.getAsInt(jsonObject, "tier", -1);
//
//        return builder.experience(experience).tier(tier);
//    }
//
//    @Override
//    public void toJson(BrewingCauldronRecipe recipe, JsonObject jsonObject) {
//        super.toJson(recipe, jsonObject);
//
//        jsonObject.addProperty("experience", recipe.experience);
//        jsonObject.addProperty("tier", recipe.tier);
//    }
//
//    @Override
//    public BrewingCauldronRecipeBuilder fromNetwork(BrewingCauldronRecipeBuilder builder, FriendlyByteBuf buf) {
//        super.fromNetwork(builder, buf);
//
//        int tier = buf.readVarInt();
//        float experience = buf.readFloat();
//
//        return builder.tier(tier).experience(experience);
//    }
//
//    @Override
//    public void toNetwork(FriendlyByteBuf buf, BrewingCauldronRecipe obj) {
//        super.toNetwork(buf, obj);
//
//        buf.writeVarInt(obj.tier);
//        buf.writeFloat(obj.experience);
//    }
//
//    public static void toNetwork(RegistryFriendlyByteBuf buffer, ShapelessProcessingRecipe recipe) {
//        buffer.writeUtf(recipe.category.name());
//        buffer.writeUtf(recipe.group);
//        buffer.writeVarInt(recipe.ingredients.length);
//        for (Ingredient ingredient : recipe.ingredients) {
//            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
//        }
//        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
//        buffer.writeVarInt(recipe.processingTime);
//    }
//
//    public static BrewingCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
//        // Base shapeless processing recipe data
//        RecipeCategory category = RecipeCategory.valueOf(buffer.readUtf());
//        String group = buffer.readUtf();
//        int ingredientCount = buffer.readVarInt();
//        Ingredient[] ingredients = new Ingredient[ingredientCount];
//        for (int i = 0; i < ingredientCount; i++) {
//            ingredients[i] = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
//        }
//        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
//        int processingTime = buffer.readVarInt();
//
//        // Additional data for brewing cauldron recipe
//        int tier = buffer.readVarInt();
//        float experience = buffer.readFloat();
//    }
//

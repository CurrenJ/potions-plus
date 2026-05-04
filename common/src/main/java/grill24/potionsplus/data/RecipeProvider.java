package grill24.potionsplus.data;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public abstract class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    protected RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public RecipeOutput getOutput() {
        return this.output;
    }

    public HolderLookup.Provider getRegistries() {
        return this.registries;
    }

    @Override
    public ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item) {
        return super.shaped(category, item);
    }

    @Override
    public ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item, int count) {
        return super.shaped(category, item, count);
    }

    @Override
    public ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item) {
        return super.shapeless(category, item);
    }

    @Override
    public ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item, int count) {
        return super.shapeless(category, item, count);
    }

    @Override
    public Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return super.has(item);
    }

    @Override
    public Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return super.has(tag);
    }
}

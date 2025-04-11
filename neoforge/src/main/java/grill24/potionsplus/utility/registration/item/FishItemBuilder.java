package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.loot.IsInBiomeCondition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class FishItemBuilder extends ItemBuilder<Item, FishItemBuilder> {
    public static final NumberProvider SMALL_SIZE = GaussianDistributionGenerator.gaussian(30F, 15F);
    public static final NumberProvider MEDIUM_SIZE = GaussianDistributionGenerator.gaussian(50F, 15F);
    public static final NumberProvider LARGE_SIZE = GaussianDistributionGenerator.gaussian(80F, 15F);

    private boolean glint;
    private SizeProvider sizeProvider;
    private List<ResourceKey<Biome>> biomes;
    private boolean canBeCaughtOutsideBiome;
    private boolean applyBiomeBonus;
    private int baseFishWeight;
    private int biomeBonusWeight;
    private int quality;

    public FishItemBuilder(String name) {
        super();
        this.name(name);
        this.properties(new Item.Properties().food(Foods.COD));
        this.itemFactory(Item::new);
        this.modelGenerator(ItemModelUtility.SimpleItemModelGenerator::new);
        this.sizeProvider(GaussianSizeBand.MEDIUM);
        this.biomes(List.of());
        this.canBeCaughtOutsideBiome(true);
        this.applyBiomeBonus(true);
        this.baseFishWeight(1);
        this.biomeBonusWeight(4);
        this.quality(1);
    }

    public FishItemBuilder(String name, String texPathPrefix) {
        this(name, ppId(texPathPrefix + name));
    }

    public FishItemBuilder(String name, ResourceLocation tex) {
        this(name);
        this.modelGenerator(holder -> new ItemModelUtility.SimpleItemModelGenerator<>(holder, tex));
    }

    public FishItemBuilder glint(boolean glint) {
        this.glint = glint;
        return this;
    }

    public FishItemBuilder sizeProvider(SizeProvider sizeProvider) {
        this.sizeProvider = sizeProvider;
        return this;
    }

    public FishItemBuilder biomes(List<ResourceKey<Biome>> biomes) {
        this.biomes = biomes;
        return this;
    }

    @SafeVarargs
    public final FishItemBuilder biomes(ResourceKey<Biome>... biomes) {
        this.biomes = List.of(biomes);
        return this;
    }

    public FishItemBuilder canBeCaughtOutsideBiome(boolean canBeCaughtOutsideBiome) {
        this.canBeCaughtOutsideBiome = canBeCaughtOutsideBiome;
        return this;
    }

    public FishItemBuilder applyBiomeBonus(boolean applyBiomeBonus) {
        this.applyBiomeBonus = applyBiomeBonus;
        return this;
    }

    public FishItemBuilder baseFishWeight(int baseFishWeight) {
        this.baseFishWeight = baseFishWeight;
        return this;
    }

    public FishItemBuilder biomeBonusWeight(int biomeBonusWeight) {
        this.biomeBonusWeight = biomeBonusWeight;
        return this;
    }

    public FishItemBuilder quality(int quality) {
        this.quality = quality;
        return this;
    }

    public static FishItemBuilder create(String name, boolean glint) {
        FishItemBuilder builder = new FishItemBuilder(name, "item/fish/");
        builder.name(name);
        builder.glint(glint);
        if (builder.glint) {
            builder.properties(new Item.Properties().food(Foods.COD).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
        }
        return builder;
    }

    public static FishItemBuilder create(String name) {
        return create(name, false);
    }

    public SizeProvider getSizeProvider() {
        return sizeProvider;
    }

    public void addAsFishingLoot(LootPool.Builder builder, BaitItemBuilder bait) {
        // Anywhere rates
        if (canBeCaughtOutsideBiome) {
            builder.add(BaitItemBuilder.whenBaitConditionMet(bait, LootItem.lootTableItem(getValue())
                    .setWeight(baseFishWeight)
                    .setQuality(quality)
                    .apply(new SetFishSizeFunction.Builder(bait.getSizeProvider(this).get())))
            );
        }

        // Biome specific rates
        int weight = biomeBonusWeight + (canBeCaughtOutsideBiome ? 0 : baseFishWeight);
        if (applyBiomeBonus) {
            builder.add(BaitItemBuilder.whenBaitConditionMet(bait, LootItem.lootTableItem(getValue())
                    .setWeight(weight)
                    .setQuality(quality)
                    .apply(new SetFishSizeFunction.Builder(bait.getSizeProvider(this).get()))
                    .when(IsInBiomeCondition.isInBiome(biomes.toArray(new ResourceKey[0]))))
            );
        }
    }

    @Override
    protected FishItemBuilder self() {
        return this;
    }

    @Override
    public boolean hasRecipeGenerator() {
        return true;
    }

    @Override
    public void generate(RecipeProvider provider, RecipeOutput output) {
        if (this.recipeGenerators != null) {
            super.generate(provider, output);
        }

        ResourceLocation recipeId = ppId(getHolder().getKey().location().getPath() + "_no_size");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, getValue())
                .requires(getValue())
                .group("fish_no_size")
                .unlockedBy("has_item", grill24.potionsplus.data.RecipeProvider.has(getValue()))
                .save(output, recipeId);
    }
}

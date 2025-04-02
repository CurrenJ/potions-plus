package grill24.potionsplus.item.builder;

import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.item.ItemModelUtility;
import grill24.potionsplus.loot.IsInBiomeCondition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.List;

public class FishItemBuilder extends ItemBuilder<Item, FishItemBuilder> {
    public static final NumberProvider SMALL_SIZE = GaussianDistributionGenerator.gaussian(30F, 15F);
    public static final NumberProvider MEDIUM_SIZE = GaussianDistributionGenerator.gaussian(50F, 15F);
    public static final NumberProvider LARGE_SIZE = GaussianDistributionGenerator.gaussian(80F, 15F);

    private boolean glint;
    private NumberProvider sizeProvider;
    private List<ResourceKey<Biome>> biomes;
    private boolean canBeCaughtOutsideBiome;
    private boolean applyBiomeBonus;
    private int baseFishWeight;
    private int biomeBonusWeight;
    private int quality;

    public FishItemBuilder() {
        super();
        this.properties(new Item.Properties().food(Foods.COD));
        this.itemFactory(Item::new);
        this.itemModelGenerator(new ItemModelUtility.SimpleItemModelGenerator());
        this.sizeProvider(MEDIUM_SIZE);
        this.biomes(List.of());
        this.canBeCaughtOutsideBiome(true);
        this.applyBiomeBonus(true);
        this.baseFishWeight(1);
        this.biomeBonusWeight(4);
        this.quality(1);
    }

    public FishItemBuilder glint(boolean glint) {
        this.glint = glint;
        return this;
    }

    public FishItemBuilder sizeProvider(NumberProvider sizeProvider) {
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
        FishItemBuilder builder = new FishItemBuilder();
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

    public NumberProvider getSizeProvider() {
        return sizeProvider;
    }

    public LootPool.Builder addAsFishingLoot(LootPool.Builder builder) {
        // Anywhere rates
        if (canBeCaughtOutsideBiome) {
            builder.add(
                    LootItem.lootTableItem(getItem())
                            .setWeight(baseFishWeight)
                            .setQuality(quality)
                            .apply(new SetFishSizeFunction.Builder(sizeProvider))
            );
        }

        // Biome specific rates
        int weight = biomeBonusWeight + (canBeCaughtOutsideBiome ? 0 : baseFishWeight);
        if (applyBiomeBonus) {
            builder.add(
                    LootItem.lootTableItem(getItem())
                            .setWeight(weight)
                            .setQuality(quality)
                            .apply(new SetFishSizeFunction.Builder(sizeProvider))
                            .when(IsInBiomeCondition.isInBiome(biomes.toArray(new ResourceKey[0])))
            );
        }
        return builder;
    }

    @Override
    FishItemBuilder self() {
        return this;
    }
}

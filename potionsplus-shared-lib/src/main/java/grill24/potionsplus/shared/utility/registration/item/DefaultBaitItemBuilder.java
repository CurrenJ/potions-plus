package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class DefaultBaitItemBuilder extends BaitItemBuilder {
    protected final int vanillaFishWeight;

    public DefaultBaitItemBuilder(String name, int vanillaFishWeight) {
        super(name);
        this.vanillaFishWeight = vanillaFishWeight;
    }

    public DefaultBaitItemBuilder(String name, String texPathPrefix, int vanillaFishWeight) {
        super(name, texPathPrefix);
        this.vanillaFishWeight = vanillaFishWeight;
    }

    public static DefaultBaitItemBuilder create(String name, int vanillaFishWeight) {
        return new DefaultBaitItemBuilder(name, "item/fish/", vanillaFishWeight);
    }

    @Override
    public LootPool.Builder generateFishingLoot(List<FishItemBuilder> fishes, LootPool.Builder builder) {
        // Add fishing loot from registered FishItemBuilders and from additional loot attached to this bait builder
        super.generateFishingLoot(fishes, builder);

        // Add vanilla fish
        addVanillaFish(builder);

        // Add custom fish from registered FishItemBuilders
        for (FishItemBuilder fishBuilder : fishes) {
            fishBuilder.addAsFishingLoot(builder, this);
        }
        return builder;
    }

    public LootPool.Builder addVanillaFish(LootPool.Builder builder) {
        return builder.add(whenBaitConditionMet(this, LootItem.lootTableItem(net.minecraft.world.item.Items.COD)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(sizeModifier.modify(GaussianSizeBand.SMALL).get()))))
                .add(whenBaitConditionMet(this, LootItem.lootTableItem(net.minecraft.world.item.Items.SALMON)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(sizeModifier.modify(GaussianSizeBand.MEDIUM).get()))))
                .add(whenBaitConditionMet(this, LootItem.lootTableItem(net.minecraft.world.item.Items.PUFFERFISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(sizeModifier.modify(GaussianSizeBand.SMALL).get()))))
                .add(whenBaitConditionMet(this, LootItem.lootTableItem(net.minecraft.world.item.Items.TROPICAL_FISH)
                        .setWeight(vanillaFishWeight)
                        .apply(new SetFishSizeFunction.Builder(sizeModifier.modify(GaussianSizeBand.MEDIUM).get()))
                        .when(IsInBiomeTagCondition.isInBiomeTag(Tags.Biomes.IS_HOT_OVERWORLD))));
    }
}

package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.item.BaitItem;
import grill24.potionsplus.loot.HasFishingRodBaitCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.List;
import java.util.function.Function;

import static grill24.potionsplus.utility.Utility.ppId;

public class BaitItemBuilder extends ItemBuilder<Item, BaitItemBuilder> {
    protected GaussianSizeBand sizeModifier;

    private Function<LootPool.Builder, LootPool.Builder> additionalFishingLoot;

    private String descriptionKey;

    public BaitItemBuilder(String name) {
        super();
        this.name(name);
        this.properties(new Item.Properties());
        this.itemFactory((prop) -> new BaitItem(prop, this.descriptionKey));
        this.modelGenerator(ItemModelUtility.SimpleItemModelGenerator::new);
        this.sizeProvider(new GaussianSizeBand(0, 0));
        this.descriptionKey = "";

        this.additionalFishingLoot = Function.identity();
    }

    public BaitItemBuilder(String name, String texPathPrefix) {
        this(name, ppId(texPathPrefix + name));
    }

    public BaitItemBuilder(String name, ResourceLocation tex) {
        this(name);
        this.modelGenerator(holder -> new ItemModelUtility.SimpleItemModelGenerator<>(holder, tex));
    }

    public static BaitItemBuilder create(String name) {
        return new BaitItemBuilder(name, ppId("item/fish/" + name));
    }

    public SizeProvider getSizeProvider(FishItemBuilder fish) {
        return fish.getSizeProvider().modify(this.sizeModifier);
    }

    public LootPool.Builder generateFishingLoot(List<FishItemBuilder> fishes, LootPool.Builder builder) {
        // Add other fishing loot
        additionalFishingLoot.apply(builder);

        return builder;
    }

    public static LootPoolEntryContainer.Builder<?> whenBaitConditionMet(BaitItemBuilder bait, LootPoolEntryContainer.Builder<?> builder) {
        if (bait.getHolder() == null || bait.getValue() == null) {
            // No item means we MUST have NO bait to catch this
            return builder.when(HasFishingRodBaitCondition.hasBait());
        }
        return builder.when(HasFishingRodBaitCondition.hasBait(bait.getHolder()));
    }

    // ----- Builder Methods -----

    public BaitItemBuilder sizeProvider(GaussianSizeBand sizeProvider) {
        this.sizeModifier = sizeProvider;
        return this;
    }

    public BaitItemBuilder fishingLoot(Function<LootPool.Builder, LootPool.Builder> additionalFishingLoot) {
        this.additionalFishingLoot = additionalFishingLoot;
        return this;
    }

    public BaitItemBuilder descriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
        return this;
    }

    @Override
    protected BaitItemBuilder self() {
        return this;
    }
}

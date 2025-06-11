package grill24.potionsplus.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.LootItemConditions;
import grill24.potionsplus.item.FishingRodDataComponent;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A LootItemCondition that checks the {@linkplain LootContextParams#TOOL tool} against an {@link ItemPredicate}.
 */
public record HasFishingRodBaitCondition(Set<Holder<Item>> items) implements LootItemCondition {
    public static final MapCodec<HasFishingRodBaitCondition> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            NeoForgeExtraCodecs.setOf(BuiltInRegistries.ITEM.holderByNameCodec()).fieldOf("items").forGetter(HasFishingRodBaitCondition::items)).apply(codecBuilder, HasFishingRodBaitCondition::new)
    );

    @Override
    public @NotNull LootItemConditionType getType() {
        return LootItemConditions.HAS_FISHING_ROD_BAIT.value();
    }

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    public boolean test(LootContext context) {
        ItemStack stack = context.getOptionalParameter(LootContextParams.TOOL);
        if (stack != null && stack.has(DataComponents.FISHING_ROD)) {
            FishingRodDataComponent fishingRodData = stack.get(DataComponents.FISHING_ROD);
            if (fishingRodData != null) {
                ItemStack bait = fishingRodData.getActiveBait();
                if (items.contains(bait.getItemHolder()) || (bait.isEmpty() && items.isEmpty())) {
                    return true;
                }
            }
        }

        return false;
    }

    @SafeVarargs
    public static Builder hasBait(Holder<Item>... items) {
        return () -> new HasFishingRodBaitCondition(Set.of(items));
    }
}

package grill24.potionsplus.behaviour.neoforge;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.alchemy.EffectRegistry;
import grill24.potionsplus.behaviour.AddMobEffectsLootBehaviour;
import grill24.potionsplus.core.neoforge.LootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AddMobEffectsLootModifier extends LootModifier {
    public static final Supplier<MapCodec<AddMobEffectsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .and(NeoForgeExtraCodecs.setOf(ResourceKey.codec(Registries.MOB_EFFECT)).optionalFieldOf("blacklistedEffects", Set.of()).forGetter(m -> m.blacklistedEffects))
            .apply(inst, AddMobEffectsLootModifier::new)
    ));

    private final Set<ResourceKey<MobEffect>> blacklistedEffects;
    private final Supplier<List<Holder<MobEffect>>> eligibleEffects;

    public AddMobEffectsLootModifier(LootItemCondition[] conditionsIn, Set<ResourceKey<MobEffect>> blacklistedEffects) {
        super(conditionsIn);
        this.blacklistedEffects = blacklistedEffects;
        this.eligibleEffects = Suppliers.memoize(() -> EffectRegistry.passiveEligible(blacklistedEffects));
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return new ObjectArrayList<>(AddMobEffectsLootBehaviour.apply(generatedLoot, context.getRandom(), eligibleEffects.get()));
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return LootModifiers.ADD_MOB_EFFECTS.value();
    }
}

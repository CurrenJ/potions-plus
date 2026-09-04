package grill24.potionsplus.behaviour.forge;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.alchemy.EffectRegistry;
import grill24.potionsplus.behaviour.AddMobEffectsLootBehaviour;
import grill24.potionsplus.core.forge.LootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AddMobEffectsLootModifier extends LootModifier {
    // Forge 52.1.2 has no NeoForgeExtraCodecs.setOf equivalent; xmap a list codec to a Set instead.
    private static final Codec<Set<ResourceKey<MobEffect>>> BLACKLIST_CODEC =
            ResourceKey.codec(Registries.MOB_EFFECT).listOf().xmap(HashSet::new, ArrayList::new);

    public static final Supplier<MapCodec<AddMobEffectsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .and(BLACKLIST_CODEC.optionalFieldOf("blacklistedEffects", Set.of()).forGetter(m -> m.blacklistedEffects))
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
        return LootModifiers.ADD_MOB_EFFECTS.get();
    }
}

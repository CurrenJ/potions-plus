package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.behaviour.AddMobEffectsLootModifier;
import grill24.potionsplus.behaviour.WormrootLootModifier;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class LootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<WormrootLootModifier>> WORMROOT = LOOT_MODIFIERS.register("wormroot_loot_modifier", WormrootLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddMobEffectsLootModifier>> ADD_MOB_EFFECTS = LOOT_MODIFIERS.register("add_mob_effects_loot_modifier", AddMobEffectsLootModifier.CODEC);
}

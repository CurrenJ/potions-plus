package grill24.potionsplus.core;

import com.mojang.serialization.Codec;
import grill24.potionsplus.behaviour.WormrootLootModifier;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID);

    public static final RegistryObject<Codec<WormrootLootModifier>> WORMROOT = LOOT_MODIFIERS.register("wormroot_loot_modifier", WormrootLootModifier.CODEC);
}

package grill24.potionsplus.core.forge;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.behaviour.forge.AddMobEffectsLootModifier;
import grill24.potionsplus.behaviour.forge.WormrootLootModifier;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID);

    public static final RegistryObject<MapCodec<WormrootLootModifier>> WORMROOT =
            LOOT_MODIFIERS.register("wormroot_loot_modifier", WormrootLootModifier.CODEC::get);
    public static final RegistryObject<MapCodec<AddMobEffectsLootModifier>> ADD_MOB_EFFECTS =
            LOOT_MODIFIERS.register("add_mob_effects_loot_modifier", AddMobEffectsLootModifier.CODEC::get);
}

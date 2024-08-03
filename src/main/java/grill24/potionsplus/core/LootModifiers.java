package grill24.potionsplus.core;

import grill24.potionsplus.behaviour.WormrootBehaviour;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootModifiers {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID);

    private static final RegistryObject<WormrootBehaviour.WormrootBehaviourSerializer> WORMROOT_LOOT_MODIFIER = LOOT_MODIFIERS.register("wormroot_loot_modifier", WormrootBehaviour.WormrootBehaviourSerializer::new);
}

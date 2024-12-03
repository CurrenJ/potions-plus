package grill24.potionsplus.core;

import grill24.potionsplus.skill.ConfiguredPlayerAbilityArgument;
import grill24.potionsplus.skill.ConfiguredSkillArgument;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, ModInfo.MOD_ID);

    private static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<ConfiguredSkillArgument>> CONFIGURED_SKILL = COMMAND_ARGUMENT_TYPES.register("configured_skill", () -> ArgumentTypeInfos.registerByClass(ConfiguredSkillArgument.class, SingletonArgumentInfo.contextAware(ConfiguredSkillArgument::new)));
    private static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<ConfiguredPlayerAbilityArgument>> ABILITY_INSTANCE = COMMAND_ARGUMENT_TYPES.register("ability_instance", () -> ArgumentTypeInfos.registerByClass(ConfiguredPlayerAbilityArgument.class, SingletonArgumentInfo.contextAware(ConfiguredPlayerAbilityArgument::new)));
}

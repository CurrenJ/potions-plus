package grill24.potionsplus.core.forge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;

public class CommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, ModInfo.MOD_ID);
}

package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Advancements {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ModInfo.MOD_ID);

    public static void init() {
        grill24.potionsplus.core.Advancements.init(TRIGGERS::register);
    }
}

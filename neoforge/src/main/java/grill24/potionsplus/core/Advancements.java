package grill24.potionsplus.core;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Advancements {
    public static DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ModInfo.MOD_ID);

    public static DeferredHolder<CriterionTrigger<?>, CreatePotionsPlusBlockTrigger> BREWING_CAULDRON_CREATION = TRIGGERS.register(CreatePotionsPlusBlockTrigger.ID.getPath(), () -> CreatePotionsPlusBlockTrigger.INSTANCE);
    public static DeferredHolder<CriterionTrigger<?>, CraftRecipeTrigger> CRAFT_RECIPE = TRIGGERS.register(CraftRecipeTrigger.ID.getPath(), () -> CraftRecipeTrigger.INSTANCE);
    public static DeferredHolder<CriterionTrigger<?>, AbyssalTroveTrigger> ABYSSAL_TROVE_TRIGGER = TRIGGERS.register(AbyssalTroveTrigger.ID.getPath(), () -> AbyssalTroveTrigger.INSTANCE);
}

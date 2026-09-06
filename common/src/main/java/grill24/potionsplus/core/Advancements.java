package grill24.potionsplus.core;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.AwardStatTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Advancements {
    // Raw trigger instances for direct use by trigger and advancement classes.
    // These must remain as concrete trigger types because Criterion<T> expects CriterionTrigger<T>,
    // not Holder<CriterionTrigger<T>>.
    public static CreatePotionsPlusBlockTrigger BREWING_CAULDRON_CREATION = CreatePotionsPlusBlockTrigger.INSTANCE;
    public static CraftRecipeTrigger CRAFT_RECIPE = CraftRecipeTrigger.INSTANCE;
    public static AbyssalTroveTrigger ABYSSAL_TROVE_TRIGGER = AbyssalTroveTrigger.INSTANCE;
    public static AwardStatTrigger AWARD_STAT_TRIGGER = AwardStatTrigger.INSTANCE;

    /**
     * Registers all criterion triggers via the provided registration function.
     * Called by platform-specific code (e.g., NeoForge PotionsPlus constructor).
     * Trigger instances are already available as static fields and singleton INSTANCE fields;
     * this method ensures they are registered in the TRIGGER_TYPE registry.
     */
    public static void init(BiFunction<String, Supplier<CriterionTrigger<?>>, Holder<CriterionTrigger<?>>> register) {
        register.apply(CreatePotionsPlusBlockTrigger.ID.getPath(), () -> CreatePotionsPlusBlockTrigger.INSTANCE);
        register.apply(CraftRecipeTrigger.ID.getPath(), () -> CraftRecipeTrigger.INSTANCE);
        register.apply(AbyssalTroveTrigger.ID.getPath(), () -> AbyssalTroveTrigger.INSTANCE);
        register.apply(AwardStatTrigger.ID.getPath(), () -> AwardStatTrigger.INSTANCE);
    }
}

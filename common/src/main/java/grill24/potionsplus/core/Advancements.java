package grill24.potionsplus.core;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.AwardStatTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;

public class Advancements {
    public static CreatePotionsPlusBlockTrigger BREWING_CAULDRON_CREATION = CreatePotionsPlusBlockTrigger.INSTANCE;
    public static CraftRecipeTrigger CRAFT_RECIPE = CraftRecipeTrigger.INSTANCE;
    public static AbyssalTroveTrigger ABYSSAL_TROVE_TRIGGER = AbyssalTroveTrigger.INSTANCE;
    public static AwardStatTrigger AWARD_STAT_TRIGGER = AwardStatTrigger.INSTANCE;
}

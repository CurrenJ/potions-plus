package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.filterhopper.HugeFilterHopperMenu;
import grill24.potionsplus.blockentity.filterhopper.LargeFilterHopperMenu;
import grill24.potionsplus.blockentity.filterhopper.SmallFilterHopperMenu;
import grill24.potionsplus.gui.skill.SkillsMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuTypes {
    public static Supplier<MenuType<SkillsMenu>> SKILLS;
    public static Supplier<MenuType<SmallFilterHopperMenu>> SMALL_FILTER_HOPPER;
    public static Supplier<MenuType<LargeFilterHopperMenu>> LARGE_FILTER_HOPPER;
    public static Supplier<MenuType<HugeFilterHopperMenu>> HUGE_FILTER_HOPPER;
}

package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.filterhopper.FilterHopperMenu;
import grill24.potionsplus.blockentity.filterhopper.LargeFilterHopperMenu;
import grill24.potionsplus.blockentity.filterhopper.SmallFilterHopperMenu;
import grill24.potionsplus.gui.skill.SkillsMenu;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Supplier;

public class MenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ModInfo.MOD_ID);

    public static final Supplier<MenuType<SkillsMenu>> SKILLS = MENU_TYPES.register("skills", () -> new MenuType<>(SkillsMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<SmallFilterHopperMenu>> SMALL_FILTER_HOPPER = MENU_TYPES.register("filter_hopper", () -> new MenuType<>(SmallFilterHopperMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<LargeFilterHopperMenu>> LARGE_FILTER_HOPPER = MENU_TYPES.register("large_filter_hopper", () -> new MenuType<>(LargeFilterHopperMenu::new, FeatureFlags.DEFAULT_FLAGS));
}

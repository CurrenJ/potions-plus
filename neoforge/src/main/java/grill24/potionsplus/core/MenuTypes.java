package grill24.potionsplus.core;

import grill24.potionsplus.gui.skill.SkillsMenu;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ModInfo.MOD_ID);

    public static final Supplier<MenuType<SkillsMenu>> SKILLS = MENU_TYPES.register("skills", () -> new MenuType<>(SkillsMenu::new, FeatureFlags.DEFAULT_FLAGS));
}

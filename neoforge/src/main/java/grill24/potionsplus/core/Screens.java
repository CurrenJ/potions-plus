package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.filterhopper.FilterHopperScreen;
import grill24.potionsplus.blockentity.filterhopper.LargeFilterHopperScreen;
import grill24.potionsplus.blockentity.filterhopper.SmallFilterHopperScreen;
import grill24.potionsplus.gui.skill.SkillsScreen;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Screens {
    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        // Register menu screens here
        event.register(MenuTypes.SKILLS.get(), SkillsScreen::new);
        event.register(MenuTypes.SMALL_FILTER_HOPPER.get(), SmallFilterHopperScreen::new);
        event.register(MenuTypes.LARGE_FILTER_HOPPER.get(), LargeFilterHopperScreen::new);
    }
}

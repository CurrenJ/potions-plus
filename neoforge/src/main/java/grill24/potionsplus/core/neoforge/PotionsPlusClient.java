package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModInfo.MOD_ID)
public class PotionsPlusClient {
    public PotionsPlusClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}

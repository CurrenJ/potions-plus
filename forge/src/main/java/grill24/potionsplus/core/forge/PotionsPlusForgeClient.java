package grill24.potionsplus.core.forge;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.item.tintsource.AnyPotionTintSource;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.slf4j.Logger;

@Mod(ModInfo.MOD_ID)
public class PotionsPlusForgeClient {
    public static final Logger LOGGER = LogUtils.getLogger();

    public PotionsPlusForgeClient(FMLModContainer container) {
        FMLClientSetupEvent.getBus(container.getModBusGroup()).addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Potions Plus (Forge) client initializing");

        // Client-side event listeners (tick/render-tick, tooltip animation, use-item). The renderer /
        // particle / tooltip-component-factory events fire during Minecraft construction (before
        // FMLClientSetupEvent), so those live in Renderers (@Mod.EventBusSubscriber, Dist.CLIENT).
        grill24.potionsplus.event.forge.ForgeClientEventListeners.register();

        // Item tint source: 26.1.2 replaced ColorProviderRegistry with data-driven ItemTintSource
        // codecs. Forge has no item-tint event (RegisterColorHandlersEvent only covers Block and
        // ColorResolvers), so put directly on the vanilla ID_MAPPER before model baking.
        ItemTintSources.ID_MAPPER.put(AnyPotionTintSource.ID, AnyPotionTintSource.CODEC);
    }
}

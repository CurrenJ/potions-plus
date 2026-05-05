package grill24.potionsplus.event;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * NeoForge-specific event handlers for clothesline rendering.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT)
public class NeoClotheslineRendererEvents {
    private static final Set<BlockPos> clotheslinesRendered = new HashSet<>();

    @SubscribeEvent
    public static void onRender(final RenderLevelStageEvent.AfterOpaqueBlocks event) {
        clotheslinesRendered.clear();
    }
}

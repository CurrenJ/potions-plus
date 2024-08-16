package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockRenderLayers {
    private static final Map<RegistryObject<Block>, RenderType> renderLayers = new HashMap<>();

    static {
        registerBlock(Blocks.DECORATIVE_FIRE, RenderType.cutout());
    }

    private static void registerBlock(RegistryObject<Block> block, RenderType renderType) {
        renderLayers.put(block, renderType);
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Grab plants/bushes from the block registry and register them as cutout render type
        Blocks.BLOCKS.getEntries().forEach(entry -> {
            Block block = entry.get();
            if (block instanceof BushBlock) {
                registerBlock(entry, RenderType.cutout());
            }
        });

        // Render layers are registered here
        renderLayers.forEach(
                (block, renderType) -> ItemBlockRenderTypes.setRenderLayer(block.get(), renderType)
        );
    }
}

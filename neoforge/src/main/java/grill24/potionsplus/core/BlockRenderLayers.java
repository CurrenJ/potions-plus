package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockRenderLayers {
    private static final Map<Holder<Block>, RenderType> renderLayers = new HashMap<>();

    static {
        registerBlock(Blocks.DECORATIVE_FIRE, RenderType.cutout());
        registerBlock(Blocks.ICICLE, RenderType.cutout());
        registerBlock(Blocks.URANIUM_GLASS, RenderType.cutout());
        registerBlock(Blocks.POTION_BEACON, RenderType.cutout());
        registerBlock(Blocks.LUMOSEED_SACKS, RenderType.solid());
        registerBlock(Blocks.SKILL_JOURNALS, RenderType.cutout());
    }

    private static void registerBlock(Holder<Block> block, RenderType renderType) {
        if (!renderLayers.containsKey(block)) {
            renderLayers.put(block, renderType);
        } else {
            PotionsPlus.LOGGER.info("Block {} already has a render layer {} registered. Will not apply {}. ", block, renderLayers.get(block), renderType);
        }
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
                (block, renderType) -> ItemBlockRenderTypes.setRenderLayer(block.value(), renderType)
        );
    }
}

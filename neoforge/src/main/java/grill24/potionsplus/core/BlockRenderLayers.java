package grill24.potionsplus.core;

import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.AbstractRegistererBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.BlockBuilder;
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
        registerBlock(DecorationBlocks.DECORATIVE_FIRE, RenderType.cutout());
        registerBlock(DecorationBlocks.ICICLE, RenderType.cutout());
        registerBlock(OreBlocks.URANIUM_GLASS, RenderType.cutout());
        registerBlock(BlockEntityBlocks.POTION_BEACON, RenderType.cutout());
        registerBlock(FlowerBlocks.LUMOSEED_SACKS, RenderType.solid());
        registerBlock(BlockEntityBlocks.SKILL_JOURNALS, RenderType.cutout());

        for (AbstractRegistererBuilder<?, ?> builder : RegistrationUtility.BUILDERS) {
            if (builder instanceof BlockBuilder<?, ?> blockBuilder) {
                switch (blockBuilder.getRenderType()) {
                    case SOLID -> registerBlock(blockBuilder.getHolder(), RenderType.solid());
                    case CUTOUT -> registerBlock(blockBuilder.getHolder(), RenderType.cutout());
                    case TRANSLUCENT -> registerBlock(blockBuilder.getHolder(), RenderType.translucent());
                }
            }
        }
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

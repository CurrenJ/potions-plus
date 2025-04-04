package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import java.util.function.Supplier;

public class ParticleEmitterBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public ParticleEmitterBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
        super(blockGetter);
    }

    public static void registerParticleEmitter(BlockStateProvider provider) {
        for (int i = 0; i < ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS.length; i++) {
            Block blockForModel = ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS[i].get().blockModel;
            blockForModel = blockForModel == null ? net.minecraft.world.level.block.Blocks.IRON_BLOCK : blockForModel;

            provider.getVariantBuilder(BlockEntityBlocks.PARTICLE_EMITTER.value())
                    .partialState().with(ParticleEmitterBlock.PARTICLE_TYPE, i).modelForState()
                    .modelFile(provider.models().getExistingFile(BlockModelUtility.mcBlock(provider, blockForModel)))
                    .addModel();
        }

        provider.simpleBlockItem(BlockEntityBlocks.PARTICLE_EMITTER.value(), provider.models().getExistingFile(provider.mcLoc("block/iron_block")));
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerParticleEmitter(provider);
    }
}

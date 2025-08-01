package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class ParticleEmitterBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public ParticleEmitterBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
        super(blockGetter);
    }

    public static void registerParticleEmitter(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        Block block = BlockEntityBlocks.PARTICLE_EMITTER.value();

        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(ParticleEmitterBlock.PARTICLE_TYPE)
                        .generate(i -> {
                            if (i < 0 || i >= ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS.length) {
                                PotionsPlus.LOGGER.warn("No configuration found for particle emitter particle type index " + i + ". Defaulting to iron block.");
                                return BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(net.minecraft.world.level.block.Blocks.IRON_BLOCK));
                            }

                            Block blockForModel = ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS[i].get().blockModel;
                            blockForModel = blockForModel == null ? net.minecraft.world.level.block.Blocks.IRON_BLOCK : blockForModel;
                            return BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(blockForModel));
                        }));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Blocks.IRON_BLOCK));
        itemModelGenerators.itemModelOutput.accept(block.asItem(), itemModel);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerParticleEmitter(blockModelGenerators, itemModelGenerators);
    }
}

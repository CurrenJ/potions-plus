package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.BloomingPlantBlock;
import grill24.potionsplus.block.VersatilePlantBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import oshi.util.tuples.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class BloomingVersatilePlantBlockModelGenerator<T, B extends Block> extends VersatilePlantBlockModelGenerator<T, B> {
    private final T[][] resources;
    public BloomingVersatilePlantBlockModelGenerator(Supplier<Holder<B>> blockGetter, T[][] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, IModelFactory<T> modelRegisterer) {
        super(blockGetter, null, texRotationFunction, itemTexture, modelRegisterer);
        this.resources = resources;
    }

    /***
     *
     * @param provider
     * @param holder
     * @param textures Textures like this: ({"block/hanging_fern_base", "block/hanging_fern_tip"}, {"block/hanging_fern_base_blooming", "block/hanging_fern_tip_blooming"})
     * @param texRotationFunction
     * @param itemTexture
     */
    public static <T> void registerBloomingPlantBlock(BlockStateProvider provider, Holder<? extends Block> holder, T[][] textures, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, VersatilePlantBlockModelGenerator.IModelFactory<T> modelGenerator) {
        if (holder.value() instanceof BloomingPlantBlock block) {
            String name = holder.getKey().location().getPath();

            // This loop generates a model for every texture used in the pattern
            Set<String> usedModels = new HashSet<>();
            for (int blooming = 0; blooming <= block.getMaxBlooming(); blooming++) {
                for (Integer textureIndex : block.getUsedTextures()) {
                    String modelName = getBloomingPlantModelName(name, blooming, textureIndex);

                    // Ensure we don't duplicate textures, since textures can be repeated in the pattern. Only need one model per texture.
                    if (!usedModels.contains(modelName)) {
                        usedModels.add(modelName);

                        T tex = textures[blooming][textureIndex];
                        modelGenerator.accept(provider, modelName, tex);
                    }
                }
            }

            provider.getVariantBuilder(block).forAllStatesExcept(state -> {
                int blooming = state.getValue(BloomingPlantBlock.BLOOMING);
                if (blooming <= block.getMaxBlooming()) {
                    Direction facing = state.getValue(VersatilePlantBlock.FACING);
                    int textureIndex = state.getValue(VersatilePlantBlock.TEXTURE_INDEX);
                    String modelName = getBloomingPlantModelName(name, blooming, textureIndex);

                    if (usedModels.contains(modelName)) {
                        Pair<Integer, Integer> texRot = texRotationFunction.apply(facing);
                        int xRotOffset = texRot.getA();
                        int yRotOffset = texRot.getB();

                        return ConfiguredModel.builder()
                                .rotationX(xRotOffset)
                                .rotationY(yRotOffset)
                                .modelFile(provider.models().getExistingFile(ppId(modelName)))
                                .build();
                    }
                }

                return ConfiguredModel.builder().modelFile(provider.models().getExistingFile(mc("block/air"))).build();
            }, VersatilePlantBlock.SEGMENT);

            VersatilePlantBlockModelGenerator.registerItem(provider, block.asItem(), itemTexture);
        }
    }

    public static String getBloomingPlantModelName(String name, int blooming, int textureIndex) {
        if (blooming >= 0 && textureIndex >= 0) {
            return name + "_b" + blooming + "_t" + textureIndex;
        }

        return name + "_b0_t0";
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerBloomingPlantBlock(provider, getHolder(), resources, texRotationFunction, itemTexture, modelRegisterer);
    }
}

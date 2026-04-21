package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.BloomingPlantBlock;
import grill24.potionsplus.block.VersatilePlantBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class BloomingVersatilePlantBlockModelGenerator<T, B extends Block> extends VersatilePlantBlockModelGenerator<T, B> {
    private final ClientModelData<T> data;

    public BloomingVersatilePlantBlockModelGenerator(Supplier<Holder<B>> blockGetter, ClientModelData<T> clientModelData) {
        super(blockGetter, new VersatilePlantBlockModelGenerator.ClientModelData<>(null, clientModelData.texRotationFunction, clientModelData.itemTexture, clientModelData.itemModel, clientModelData.useTint, clientModelData.modelRegisterer));
        this.data = clientModelData;
    }

    public static <T> void registerBloomingPlantBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Holder<? extends Block> holder, ClientModelData<T> data) {
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

                        T tex = data.resources[blooming][textureIndex];
                        data.modelRegisterer.accept(blockModelGenerators, itemModelGenerators, modelName, tex);
                    }
                }
            }

            BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(VersatilePlantBlock.FACING, VersatilePlantBlock.TEXTURE_INDEX, BloomingPlantBlock.BLOOMING)
                            .generate((facing, textureIndex, blooming) -> {
                                        String modelName = getBloomingPlantModelName(name, blooming, textureIndex);

                                        if (usedModels.contains(modelName)) {
                                            VariantMutator rotationMutator = data.texRotationFunction.apply(facing);
                                            return BlockModelGenerators.plainVariant(ppId(modelName)).with(rotationMutator);
                                        }

                                        return BlockModelGenerators.plainVariant(mc("block/air"));
                                    }
                            ));
            blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

            VersatilePlantBlockModelGenerator.registerItem(blockModelGenerators, itemModelGenerators, block.asItem(), data.itemTexture, data.itemModel, data.useTint);
        }
    }

    public static String getBloomingPlantModelName(String name, int blooming, int textureIndex) {
        if (blooming >= 0 && textureIndex >= 0) {
            return "block/" + name + "_b" + blooming + "_t" + textureIndex;
        }

        return "block/" + name + "_b0_t0";
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerBloomingPlantBlock(blockModelGenerators, itemModelGenerators, getHolder(), data);
    }

    public record ClientModelData<T>(T[][] resources, Function<Direction, VariantMutator> texRotationFunction,
                                     ResourceLocation itemTexture, ResourceLocation itemModel, boolean useTint,
                                     IModelFactory<T> modelRegisterer) {
        public static class Builder<T> {
            private T[][] resources;
            private Function<Direction, VariantMutator> texRotationFunction;
            private ResourceLocation itemTexture;
            private ResourceLocation itemModel;
            private boolean useTint;
            private IModelFactory<T> modelRegisterer;

            public Builder<T> resources(T[][] resources) {
                this.resources = resources;
                return this;
            }

            public Builder<T> texRotationFunction(Function<Direction, VariantMutator> texRotationFunction) {
                this.texRotationFunction = texRotationFunction;
                return this;
            }

            public Builder<T> itemTexture(ResourceLocation itemTexture) {
                this.itemTexture = itemTexture;
                return this;
            }

            public Builder<T> itemModel(ResourceLocation itemModel) {
                this.itemModel = itemModel;
                return this;
            }

            public Builder<T> useTint(boolean useTint) {
                this.useTint = useTint;
                return this;
            }

            public Builder<T> modelRegisterer(IModelFactory<T> modelRegisterer) {
                this.modelRegisterer = modelRegisterer;
                return this;
            }

            public ClientModelData<T> build() {
                if (resources == null || resources.length == 0) {
                    throw new IllegalStateException("Resources must be provided and cannot be empty.");
                }

                if (texRotationFunction == null) {
                    throw new IllegalStateException("Texture rotation function must be provided.");
                }

                if (itemTexture == null && itemModel == null) {
                    throw new IllegalStateException("Either item texture or item model must be provided.");
                }

                if (modelRegisterer == null) {
                    throw new IllegalStateException("Model registerer must be provided.");
                }

                return new ClientModelData<>(resources, texRotationFunction, itemTexture, itemModel, useTint, modelRegisterer);
            }
        }
    }
}

package grill24.potionsplus.utility.registration.block;

import com.mojang.math.Quadrant;
import grill24.potionsplus.block.VersatilePlantBlock;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class VersatilePlantBlockModelGenerator<T, I extends Block> extends BlockModelUtility.BlockModelGenerator<I> {
    private final ClientModelData<T> data;

    public static final Function<Direction, VariantMutator> HANGING_PLANT_TEX_ORIENTATION = (facing) -> {
        Quadrant xRotOffset = switch (facing) {
            case UP -> Quadrant.R180;
            case DOWN -> Quadrant.R0;
            default -> Quadrant.R90;
        };
        Quadrant yRotOffset = switch (facing) {
            case NORTH -> Quadrant.R180;
            case EAST -> Quadrant.R270;
            case SOUTH -> Quadrant.R0;
            case WEST -> Quadrant.R90;
            default -> Quadrant.R0;
        };
        return VariantMutator.X_ROT.withValue(xRotOffset).then(VariantMutator.Y_ROT.withValue(yRotOffset));
    };
    public static final Function<Direction, VariantMutator> UPRIGHT_PLANT_TEX_ORIENTATION = (facing) -> {
        Quadrant xRotOffset = switch (facing) {
            case UP -> Quadrant.R0;
            case DOWN -> Quadrant.R180;
            default -> Quadrant.R90;
        };
        Quadrant yRotOffset = switch (facing) {
            case NORTH -> Quadrant.R0;
            case EAST -> Quadrant.R90;
            case SOUTH -> Quadrant.R180;
            case WEST -> Quadrant.R270;
            default -> Quadrant.R0;
        };
        return VariantMutator.X_ROT.withValue(xRotOffset).then(VariantMutator.Y_ROT.withValue(yRotOffset));
    };

    public interface IModelFactory<T> {
        void accept(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, String modelName, T resource);
    }

    public static final TextureSlot LUMOSEED_VINE = TextureSlot.create("vine", TextureSlot.ALL);
    public static final TextureSlot LUMOSEED_SACK = TextureSlot.create("sack", TextureSlot.ALL);
    public static final Function<ResourceLocation, ModelTemplate> LUMOSEED_MODEL_TEMPLATES = (parentModel) -> new ModelTemplate(
            Optional.of(parentModel),
            Optional.of("lumoseed"),
            LUMOSEED_VINE,
            LUMOSEED_SACK
    );

    public static final IModelFactory<ResourceLocation> CROSS_MODEL_GENERATOR = (blockModelGenerators, itemModelGenerators, modelName, resource) ->
            ModelTemplates.CROSS.create(ppId(modelName), new TextureMapping().put(TextureSlot.CROSS, resource), blockModelGenerators.modelOutput);
    public static final VersatilePlantBlockModelGenerator.IModelFactory<Pair<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>> LUMOSEED_MODEL_GENERATOR = (blockModelGenerators, itemModelGenerators, modelName, resources) ->
            LUMOSEED_MODEL_TEMPLATES.apply(resources.getA()).create(ppId(modelName),
                    new TextureMapping().put(LUMOSEED_VINE, resources.getB().getA()).put(LUMOSEED_SACK, resources.getB().getB()),
                    blockModelGenerators.modelOutput);
    public static final VersatilePlantBlockModelGenerator.IModelFactory<ResourceLocation> PARENTED_MODEL_GENERATOR = (blockModelGenerators, itemModelGenerators, modelName, parentModel) ->
            new ModelTemplate(Optional.of(parentModel), Optional.empty()).create(ppId(modelName), new TextureMapping(), blockModelGenerators.modelOutput);


    public VersatilePlantBlockModelGenerator(Supplier<Holder<I>> blockGetter, ClientModelData<T> clientModelData) {
        super(blockGetter);
        this.data = clientModelData;
    }

    public static String getVersatilePlantModelName(String name, int textureIndex) {
        if (textureIndex >= 0) {
            return "block/" + name + "_t" + textureIndex;
        }

        return "block/" + name + "_t0";
    }

    public static <T> void registerVersatilePlantBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Holder<? extends Block> holder, ClientModelData<T> data) {
        Block b = holder.value();
        if (b instanceof VersatilePlantBlock block) {

            String name = holder.getKey().location().getPath();

            // This loop generates a model for every texture used in the pattern
            Set<String> usedModels = new HashSet<>();
            for (Integer textureIndex : block.getUsedTextures()) {
                String modelName = getVersatilePlantModelName(name, textureIndex);

                // Ensure we don't duplicate textures, since textures can be repeated in the pattern. Only need one model per texture.
                if (!usedModels.contains(modelName)) {
                    usedModels.add(modelName);

                    T resource = data.resources[textureIndex];
                    data.modelRegisterer.accept(blockModelGenerators, itemModelGenerators, modelName, resource);
                }
            }

            BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(VersatilePlantBlock.FACING, VersatilePlantBlock.TEXTURE_INDEX)
                            .generate((facing, textureIndex) -> {
                                String modelName = getVersatilePlantModelName(name, textureIndex);

                                if (usedModels.contains(modelName)) {
                                    VariantMutator rotationMutator = data.texRotationFunction.apply(facing);
                                    return BlockModelGenerators.plainVariant(ppId(modelName)).with(rotationMutator);
                                }

                                return BlockModelGenerators.plainVariant(mc("block/air"));
                            }));
            blockModelGenerators.blockStateOutput.accept(blockstateGenerator);

            registerItem(blockModelGenerators, itemModelGenerators, block.asItem(), data.itemTexture, data.itemModel, data.useTint);
        }
    }

    public static void registerItem(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Item item, @Nullable ResourceLocation texture, @Nullable ResourceLocation model, boolean useTint) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        if (texture != null) {
            // Generate item model
            ModelTemplates.FLAT_ITEM.create(modelLocation, new TextureMapping().put(TextureSlot.LAYER0, texture), blockModelGenerators.modelOutput);

            // Generate client item definition
            itemModelGenerators.itemModelOutput.accept(
                    item,
                    new BlockModelWrapper.Unbaked(
                            modelLocation,
                            useTint ? List.of(new GrassColorSource())
                                    : Collections.emptyList()
                    ));
        } else if (model != null) {
            // If no texture is provided, use block model as item model
            if (item instanceof BlockItem blockItem) {
                itemModelGenerators.itemModelOutput.accept(
                        item,
                        new BlockModelWrapper.Unbaked(
                                model,
                                useTint ? List.of(new GrassColorSource())
                                        : Collections.emptyList()
                        )
                );
            }
        }
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerVersatilePlantBlock(blockModelGenerators, itemModelGenerators, getHolder(), data);
    }

    public record ClientModelData<T>(T[] resources, Function<Direction, VariantMutator> texRotationFunction,
                                     @Nullable ResourceLocation itemTexture, @Nullable ResourceLocation itemModel,
                                     boolean useTint, IModelFactory<T> modelRegisterer) {
        public static class Builder<T> {
            public T[] resources;
            public Function<Direction, VariantMutator> texRotationFunction = VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION;
            public ResourceLocation itemTexture = null;
            public ResourceLocation itemModel = null;
            public boolean useTint = false;
            public IModelFactory<T> modelRegisterer;

            public Builder<T> resources(T[] resources) {
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

            public Builder<T> useTint() {
                this.useTint = true;
                return this;
            }

            public Builder<T> modelRegisterer(IModelFactory<T> modelRegisterer) {
                this.modelRegisterer = modelRegisterer;
                return this;
            }

            public ClientModelData<T> build() {
                if (resources == null || resources.length == 0) {
                    throw new IllegalArgumentException("Resources must not be null or empty");
                }

                if (modelRegisterer == null) {
                    throw new IllegalArgumentException("Model registerer must not be null");
                }

                if (texRotationFunction == null) {
                    throw new IllegalArgumentException("Texture rotation function must not be null");
                }

                if (itemTexture == null && itemModel == null) {
                    throw new IllegalArgumentException("Either itemTexture or itemModel must be provided");
                }

                return new ClientModelData<>(resources, texRotationFunction, itemTexture, itemModel, useTint, modelRegisterer);
            }
        }
    }
}

package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.VersatilePlantBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class VersatilePlantBlockModelGenerator<T, I extends Block> extends BlockModelUtility.BlockModelGenerator<I> {
    private final T[] resources;
    protected final Function<Direction, Pair<Integer, Integer>> texRotationFunction;
    protected final ResourceLocation itemTexture;
    protected final IModelFactory<T> modelRegisterer;

    public static final Function<Direction, Pair<Integer, Integer>> HANGING_PLANT_TEX_ORIENTATION = (facing) -> {
        int xRotOffset = switch (facing) {
            case UP -> 180;
            case DOWN -> 0;
            default -> 90;
        };
        int yRotOffset = switch (facing) {
            case NORTH -> 180;
            case EAST -> 270;
            case SOUTH -> 0;
            case WEST -> 90;
            default -> 0;
        };
        return new Pair<>(xRotOffset, yRotOffset);
    };
    public static final Function<Direction, Pair<Integer, Integer>> UPRIGHT_PLANT_TEX_ORIENTATION = (facing) -> {
        int xRotOffset = switch (facing) {
            case UP -> 0;
            case DOWN -> 180;
            default -> 90;
        };
        int yRotOffset = switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        return new Pair<>(xRotOffset, yRotOffset);
    };

    public interface IModelFactory<T> {
        void accept(BlockStateProvider provider, String modelName, T resource);
    }

    public static final IModelFactory<ResourceLocation> CROSS_MODEL_GENERATOR = (provider, modelName, resource) ->
            provider.models().withExistingParent(modelName, provider.mcLoc("block/cross")).texture("cross", resource);
    public static final VersatilePlantBlockModelGenerator.IModelFactory<Pair<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>> LUMOSEED_MODEL_GENERATOR = (provider, modelName, resources) ->
            provider.models().withExistingParent(modelName, resources.getA()).texture("vine", resources.getB().getA()).texture("sack", resources.getB().getB());
    public static final VersatilePlantBlockModelGenerator.IModelFactory<ResourceLocation> PARENTED_MODEL_GENERATOR = (provider, modelName, parentModel) ->
            provider.models().withExistingParent(modelName, parentModel);


    public VersatilePlantBlockModelGenerator(Supplier<Holder<I>> blockGetter, T[] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, IModelFactory<T> modelRegisterer) {
        super(blockGetter);
        this.resources = resources;
        this.texRotationFunction = texRotationFunction;
        this.itemTexture = itemTexture;
        this.modelRegisterer = modelRegisterer;
    }

    public static String getVersatilePlantModelName(String name, int textureIndex) {
        if (textureIndex >= 0) {
            return name + "_t" + textureIndex;
        }

        return name + "_t0";
    }

    public static <T> void registerVersatilePlantBlock(BlockStateProvider provider, Holder<? extends Block> holder, T[] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, IModelFactory<T> modelRegisterer) {
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

                    T resource = resources[textureIndex];
                    modelRegisterer.accept(provider, modelName, resource);
                }
            }

            provider.getVariantBuilder(block).forAllStatesExcept(state -> {
                Direction facing = state.getValue(VersatilePlantBlock.FACING);
                int textureIndex = state.getValue(VersatilePlantBlock.TEXTURE_INDEX);
                String modelName = getVersatilePlantModelName(name, textureIndex);

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
                return ConfiguredModel.builder().modelFile(provider.models().getExistingFile(mc("block/air"))).build();
            }, VersatilePlantBlock.SEGMENT);

            registerItem(provider, block.asItem(), itemTexture);
        }
    }

    public static void registerItem(BlockStateProvider provider, Item item, ResourceLocation texture) {
        ResourceLocation modelLocation = getModelLocation(item);
        provider.itemModels().getBuilder(modelLocation.getPath())
                .parent(provider.models().getExistingFile(provider.mcLoc("item/generated")))
                .texture("layer0", texture);
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerVersatilePlantBlock(provider, getHolder(), resources, texRotationFunction, itemTexture, modelRegisterer);
    }
}

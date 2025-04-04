package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import java.util.Objects;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class BlockModelUtility {
    public static ResourceLocation mcBlock(BlockStateProvider provider, Block block) {
        return provider.mcLoc(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath());
    }

    public abstract static class BlockModelGenerator<T extends Block> implements IModelGenerator<T> {
        private final Supplier<Holder<T>> blockGetter;

        public BlockModelGenerator(Supplier<Holder<T>> blockGetter) {
            this.blockGetter = blockGetter;
        }

        @Override
        public Holder<? extends T> getHolder() {
            return blockGetter.get();
        }
    }

    public static class CubeAllBlockModelGenerator<T extends Block> extends BlockModelGenerator<T> {
        private final ResourceLocation textureLocation;

        private final boolean generateBlockModel;
        private final boolean generateBlockStates;
        private final boolean generateItemModel;

        public CubeAllBlockModelGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation textureLocation, boolean generateBlockModel, boolean generateBlockStates, boolean generateItemModel) {
            super(blockGetter);
            this.textureLocation = textureLocation;
            this.generateBlockModel = generateBlockModel;
            this.generateBlockStates = generateBlockStates;
            this.generateItemModel = generateItemModel;
        }

        public CubeAllBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
            this(blockGetter, null, true, true, true);
        }

        @Override
        public void generate(BlockStateProvider provider) {
            ResourceLocation modelLocation = getModelLocation(getHolder().value());

            if (generateBlockModel) {
                // Generate the block model
                provider.models().getBuilder(modelLocation.getPath())
                        .parent(provider.models().getExistingFile(mc("block/cube_all")))
                        .texture("all", textureLocation != null ? textureLocation : modelLocation);
            }
            if (generateBlockStates) {
                // Generate the blockstates
                provider.getVariantBuilder(getHolder().value()).forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(provider.models().getExistingFile(modelLocation))
                        .build());
            }

            if (generateItemModel) {
                // Generate the item model
                provider.simpleBlockItem(getHolder().value(), provider.models().getExistingFile(modelLocation));
            }
        }
    }

    public static class OnlyBlockItemModelGenerator<T extends Block> extends CubeAllBlockModelGenerator<T> {
        public OnlyBlockItemModelGenerator(Supplier<Holder<T>> blockGetter) {
            super(blockGetter, null, false, false, true);
        }
    }

    /**
     * Generates a block model from a model .json file.
     */
    public static class FromModelFileBlockStateGenerator<T extends Block> implements IModelGenerator<T> {
        private final Supplier<Holder<T>> blockGetter;
        private final ResourceLocation modelLocation;
        private final boolean generateBlockStates;
        private final boolean generateItemModel;

        public FromModelFileBlockStateGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation modelLocation, boolean generateBlockStates, boolean generateItemModel) {
            this.blockGetter = blockGetter;
            this.modelLocation = modelLocation;
            this.generateItemModel = generateItemModel;
            this.generateBlockStates = generateBlockStates;
        }

        public FromModelFileBlockStateGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation modelLocation) {
            this(blockGetter, modelLocation, true, true);
        }

        @Override
        public void generate(BlockStateProvider provider) {
            // Generate the blockstates
            if (generateBlockStates) {
                provider.getVariantBuilder(getHolder().value()).forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(provider.models().getExistingFile(modelLocation))
                        .build());
            }

            // Generate the item model
            if (generateItemModel) {
                provider.simpleBlockItem(getHolder().value(), provider.models().getExistingFile(modelLocation));
            }
        }

        @Override
        public Holder<? extends T> getHolder() {
            return blockGetter.get();
        }
    }
}

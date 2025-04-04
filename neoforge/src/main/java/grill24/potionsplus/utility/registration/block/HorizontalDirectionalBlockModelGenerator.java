package grill24.potionsplus.utility.registration.block;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import java.util.function.Supplier;

public class HorizontalDirectionalBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    private final ResourceLocation modelLocation;

    public HorizontalDirectionalBlockModelGenerator(Supplier<Holder<T>> blockGetter) {
        super(blockGetter);
        this.modelLocation = null;
    }

    public HorizontalDirectionalBlockModelGenerator(Supplier<Holder<T>> blockGetter, ResourceLocation modelLocation) {
        super(blockGetter);
        this.modelLocation = modelLocation;
    }

    public static void registerHorizontalDirectionalBlock(BlockStateProvider provider, Block block, ResourceLocation model) {
        provider.getVariantBuilder(block).forAllStates(state -> {
            int yRot = switch (state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING)) {
                case NORTH -> 180;
                case EAST -> 270;
                case SOUTH -> 0;
                case WEST -> 90;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(provider.models().getExistingFile(model))
                    .rotationY(yRot)
                    .build();
        });
    }

    public static void registerHorizontalDirectionalBlock(BlockStateProvider provider, Block block) {
        registerHorizontalDirectionalBlock(provider, block, BuiltInRegistries.BLOCK.getKey(block));
    }

    @Override
    public void generate(BlockStateProvider provider) {
        if (modelLocation == null) {
            registerHorizontalDirectionalBlock(provider, getHolder().value());
        } else {
            registerHorizontalDirectionalBlock(provider, getHolder().value(), modelLocation);
        }
    }
}

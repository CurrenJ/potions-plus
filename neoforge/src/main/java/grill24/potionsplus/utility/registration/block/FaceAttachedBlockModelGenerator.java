package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import java.util.function.Supplier;

public class FaceAttachedBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public FaceAttachedBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    public static void registerFaceAttachedHorizontalDirectionalBlock(BlockStateProvider provider, Block block) {
        provider.getVariantBuilder(block).forAllStates(state -> {
            int yRot = switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            int xRot = switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
                case FLOOR -> 0;
                case WALL -> 90;
                case CEILING -> 180;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(provider.models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)))
                    .rotationY(yRot)
                    .rotationX(xRot)
                    .uvLock(true)
                    .build();
        });
    }

    @Override
    public void generate(BlockStateProvider provider) {
        registerFaceAttachedHorizontalDirectionalBlock(provider, getHolder().value());
    }
}

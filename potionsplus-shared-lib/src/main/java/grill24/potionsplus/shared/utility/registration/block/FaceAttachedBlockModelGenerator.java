package grill24.potionsplus.utility.registration.block;

import com.mojang.math.Quadrant;
import grill24.potionsplus.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;

import java.util.function.Function;
import java.util.function.Supplier;

public class FaceAttachedBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public FaceAttachedBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    public static void registerFaceAttachedHorizontalDirectionalBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block) {
        ResourceLocation modelName = ModelLocationUtils.getModelLocation(block);
        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(HorizontalDirectionalBlock.FACING, FaceAttachedHorizontalDirectionalBlock.FACE)
                        .generate((facing, face) -> {
                            VariantMutator rotationMutator = Y_ROT_MUTATOR.apply(facing).then(X_ROT_MUTATOR.apply(face)).then(VariantMutator.UV_LOCK.withValue(true));
                            return BlockModelGenerators.plainVariant(modelName).with(rotationMutator);
                        }));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
    }

    private static final Function<Direction, VariantMutator> Y_ROT_MUTATOR = (facing) -> {
        Quadrant yRotOffset = switch (facing) {
            case NORTH -> Quadrant.R0;
            case EAST -> Quadrant.R90;
            case SOUTH -> Quadrant.R180;
            case WEST -> Quadrant.R270;
            default -> Quadrant.R0;
        };
        return VariantMutator.Y_ROT.withValue(yRotOffset);
    };

    private static final Function<AttachFace, VariantMutator> X_ROT_MUTATOR = (face) -> {
        Quadrant xRotOffset = switch (face) {
            case FLOOR -> Quadrant.R0;
            case WALL -> Quadrant.R90;
            case CEILING -> Quadrant.R180;
        };
        return VariantMutator.X_ROT.withValue(xRotOffset);
    };

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerFaceAttachedHorizontalDirectionalBlock(blockModelGenerators, itemModelGenerators, getHolder().value());
    }
}

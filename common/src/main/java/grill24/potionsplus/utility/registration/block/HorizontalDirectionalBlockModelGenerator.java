package grill24.potionsplus.utility.registration.block;

import com.mojang.math.Quadrant;
import grill24.potionsplus.block.HorizontalDirectionalBlock;
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

import java.util.function.Function;
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

    private static final Function<Direction, VariantMutator> Y_ROT_MUTATOR = facing -> {
        Quadrant yRot = switch (facing) {
            case NORTH -> Quadrant.R180;
            case EAST -> Quadrant.R270;
            case SOUTH -> Quadrant.R0;
            case WEST -> Quadrant.R90;
            default -> Quadrant.R0;
        };

        return VariantMutator.Y_ROT.withValue(yRot);
    };

    public static void registerHorizontalDirectionalBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block, ResourceLocation model) {
        BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(HorizontalDirectionalBlock.FACING).generate(facing -> {
                    VariantMutator rotationMutator = Y_ROT_MUTATOR.apply(facing);
                    return BlockModelGenerators.plainVariant(model).with(rotationMutator);
                }));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
    }

    public static void registerHorizontalDirectionalBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block) {
        registerHorizontalDirectionalBlock(blockModelGenerators, itemModelGenerators, block, ModelLocationUtils.getModelLocation(block));
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        if (modelLocation == null) {
            registerHorizontalDirectionalBlock(blockModelGenerators, itemModelGenerators, getHolder().value());
        } else {
            registerHorizontalDirectionalBlock(blockModelGenerators, itemModelGenerators, getHolder().value(), modelLocation);
        }
    }
}

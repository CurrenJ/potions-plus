package grill24.potionsplus.block;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.utility.ShapeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FaceAttachedHorizontalDirectionalBlock extends net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock {
    MapCodec<FaceAttachedHorizontalDirectionalBlock> CODEC = simpleCodec(FaceAttachedHorizontalDirectionalBlock::new);

    protected VoxelShape[] shapes;

    public FaceAttachedHorizontalDirectionalBlock(Properties p_53182_) {
        super(p_53182_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public FaceAttachedHorizontalDirectionalBlock(Properties p_53182_, VoxelShape onGroundShape) {
        super(p_53182_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
        this.shapes = generateShapes(onGroundShape);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51101_) {
        p_51101_.add(FACING, FACE);
    }

    protected static VoxelShape[] generateShapes(VoxelShape onGroundShape) {
        VoxelShape[] voxelShapes = new VoxelShape[6];

        voxelShapes[0] = onGroundShape; // Up
        voxelShapes[1] = ShapeUtility.rotateVoxelShape(onGroundShape, 180, Direction.Axis.X); // Down

        VoxelShape temp = ShapeUtility.rotateVoxelShape(onGroundShape, 90, Direction.Axis.X);
        voxelShapes[2] = temp; // North
        voxelShapes[3] = ShapeUtility.rotateVoxelShape(temp, 90, Direction.Axis.Y); // East
        voxelShapes[4] = ShapeUtility.rotateVoxelShape(temp, 180, Direction.Axis.Y); // South
        voxelShapes[5] = ShapeUtility.rotateVoxelShape(temp, 270, Direction.Axis.Y); // West

        return voxelShapes;
    }

    protected static Vec3[] generateRotations(Vec3 point) {
        Vec3[] rotations = new Vec3[6];

        rotations[0] = point; // Up
        rotations[1] = ShapeUtility.rotatePoint(point, 180, Direction.Axis.X); // Down

        Vec3 temp = ShapeUtility.rotatePoint(point, 90, Direction.Axis.X);
        rotations[2] = temp; // North
        rotations[3] = ShapeUtility.rotatePoint(temp, 90, Direction.Axis.Y); // East
        rotations[4] = ShapeUtility.rotatePoint(temp, 180, Direction.Axis.Y); // South
        rotations[5] = ShapeUtility.rotatePoint(temp, 270, Direction.Axis.Y); // West

        return rotations;
    }

    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return getDirectionalValue(state, shapes);
    }

    protected <T> T getDirectionalValue(BlockState state, T[] values) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> values[0];
            case CEILING -> values[1];
            case WALL -> switch (state.getValue(FACING)) {
                case NORTH -> values[2];
                case EAST -> values[3];
                case SOUTH -> values[4];
                case WEST -> values[5];
                default -> values[0];
            };
        };
    }

    protected BlockPos getAttachedTo(BlockState state, BlockPos pos) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> pos.below();
            case CEILING -> pos.above();
            case WALL -> pos.relative(state.getValue(FACING).getOpposite());
        };
    }
}

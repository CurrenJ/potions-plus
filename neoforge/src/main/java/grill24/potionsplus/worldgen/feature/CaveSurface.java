package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum CaveSurface implements StringRepresentable {
    CEILING(Direction.UP, new BlockPos(0, 1, 0), "ceiling"),
    FLOOR(Direction.DOWN, new BlockPos(0, -1, 0), "floor"),
    WALL_EAST(Direction.EAST, new BlockPos(1, 0, 0), "wall_east"),
    WALL_SOUTH(Direction.SOUTH, new BlockPos(0, 0, 1), "wall_south"),
    WALL_WEST(Direction.WEST, new BlockPos(-1, 0, 0), "wall_west"),
    WALL_NORTH(Direction.NORTH, new BlockPos(0, 0, -1), "wall_north");

    public static final Codec<CaveSurface> CODEC = StringRepresentable.fromEnum(CaveSurface::values);
    private final Direction direction;
    private final BlockPos vector;
    private final String id;

    CaveSurface(Direction direction, BlockPos vector, String id) {
        this.direction = direction;
        this.vector = vector;
        this.id = id;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public BlockPos getVector() {
        return this.vector;
    }

    public String getSerializedName() {
        return this.id;
    }
}


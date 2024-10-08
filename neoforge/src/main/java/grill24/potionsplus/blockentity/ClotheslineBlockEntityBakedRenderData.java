package grill24.potionsplus.blockentity;

import org.joml.Vector3f;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.render.LeashRenderer;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ClotheslineBlockEntityBakedRenderData {
    private static Map<Direction, ClotheslineRendererData> bakedData = generateBakedRenderData();

    private static Map<Direction, ClotheslineRendererData> generateBakedRenderData() {
        Map<Direction, ClotheslineRendererData> bakedData = new HashMap<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            bakedData.put(direction, new ClotheslineRendererData(direction));
        }
        return bakedData;
    }

    public static ClotheslineRendererData getBakedRenderData(BlockState state) {
        if (!state.hasProperty(ClotheslineBlock.FACING)) {
            // warn
            PotionsPlus.LOGGER.warn("Tried to get baked render data for a clothesline block without a facing property");
            return null;
        }

        return bakedData.get(state.getValue(ClotheslineBlock.FACING));
    }

    // ----- Maths -----

    public static class ClotheslineRendererData {
        public final Vector3f[][] leashPoints;
        public final Vector3f[][] itemPoints;

        public ClotheslineRendererData(Direction direction) {
            this.leashPoints = calculateAllLeashPoints(direction);
            this.itemPoints = calculateAllItemPoints(leashPoints);
        }

        private Vector3f[] getLeashPoints(int distance) {
            return leashPoints[distance - ClotheslineBlockEntity.MIN_DISTANCE];
        }

        public Vector3f[] getItemPoints(int distance) {
            return itemPoints[distance - ClotheslineBlockEntity.MIN_DISTANCE];
        }

        public Vector3f getItemPoint(BlockPos pos, BlockState state, int slot, boolean worldSpace) {
            if (!state.is(Blocks.CLOTHESLINE.value()) || !state.hasProperty(ClotheslineBlock.DISTANCE)) {
                PotionsPlus.LOGGER.warn("Tried to get item point for a clothesline block without a distance property!");
                return null;
            }

            int distance = state.getValue(ClotheslineBlock.DISTANCE);

            Vector3f point = new Vector3f(getItemPoints(distance)[slot]);
            if (worldSpace) {
                point.add(pos.getX(), pos.getY(), pos.getZ());
                point.add(ClotheslineBlockEntityRenderer.ITEM_OFFSET);
                return point;
            } else {
                return point;
            }
        }
    }

    public static Vector3f getItemPoint(BlockPos pos, BlockState state, int slot, boolean worldSpace) {
        if (!state.is(Blocks.CLOTHESLINE.value()) || !state.hasProperty(ClotheslineBlock.FACING)) {
            PotionsPlus.LOGGER.warn("Tried to get item point for a clothesline block without a facing property!");
            return null;
        }

        return bakedData.get(state.getValue(ClotheslineBlock.FACING)).getItemPoint(pos, state, slot, worldSpace);
    }

    private static Vector3f[][] calculateAllLeashPoints(Direction direction) {
        Vector3f[][] leashPoints = new Vector3f[ClotheslineBlockEntity.MAX_DISTANCE - ClotheslineBlockEntity.MIN_DISTANCE + 1][];

        for (int distance = ClotheslineBlockEntity.MIN_DISTANCE; distance <= ClotheslineBlockEntity.MAX_DISTANCE; distance++) {
            BlockPos from = BlockPos.ZERO;
            BlockPos to = switch (direction) {
                case NORTH -> from.relative(Direction.EAST, distance);
                case SOUTH -> from.relative(Direction.WEST, distance);
                case WEST -> from.relative(Direction.NORTH, distance);
                case EAST -> from.relative(Direction.SOUTH, distance);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            leashPoints[distance - ClotheslineBlockEntity.MIN_DISTANCE] = LeashRenderer.calculateLeashPoints(Vec3.atLowerCornerOf(from), Vec3.atLowerCornerOf(to));
        }

        return leashPoints;
    }

    private static Vector3f[][] calculateAllItemPoints(Vector3f[][] leashPoints) {
        Vector3f[][] itemPoints = new Vector3f[ClotheslineBlockEntity.MAX_DISTANCE - ClotheslineBlockEntity.MIN_DISTANCE + 1][];

        for (int distance = ClotheslineBlockEntity.MIN_DISTANCE; distance <= ClotheslineBlockEntity.MAX_DISTANCE; distance++) {
            int index = distance - ClotheslineBlockEntity.MIN_DISTANCE;
            int slots = ClotheslineBlockEntity.getItemsForClotheslineDistance(distance);
            float leashPointsPerItemRendered = (float) leashPoints[index].length / (slots + 1);

            itemPoints[index] = new Vector3f[slots];
            for (int slot = 0; slot < slots; slot++) {
                float i = leashPointsPerItemRendered * (slot + 1);
                Vector3f a = leashPoints[index][(int) i];
                Vector3f b = leashPoints[index][(int) i + 1];
                Vector3f itemPoint = RUtil.lerp3f(a, b, i % 1);
                itemPoint.add(ClotheslineBlockEntityRenderer.OFFSET_IN_POST_BLOCKS);
                itemPoints[index][slot] = itemPoint;
            }
        }

        return itemPoints;
    }
}

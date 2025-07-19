package grill24.potionsplus.utility;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class ShapeUtility {
    public static VoxelShape rotateVoxelShape(VoxelShape shape, int angle, Direction.Axis axis) {
        List<VoxelShape> shapes = new ArrayList<>();

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double[] rotatedMin = rotatePoint(minX, minY, minZ, angle, axis);
            double[] rotatedMax = rotatePoint(maxX, maxY, maxZ, angle, axis);

            // Sort new coordinates to ensure min and max are correct
            double newMinX = Math.min(rotatedMin[0], rotatedMax[0]);
            double newMinY = Math.min(rotatedMin[1], rotatedMax[1]);
            double newMinZ = Math.min(rotatedMin[2], rotatedMax[2]);
            double newMaxX = Math.max(rotatedMin[0], rotatedMax[0]);
            double newMaxY = Math.max(rotatedMin[1], rotatedMax[1]);
            double newMaxZ = Math.max(rotatedMin[2], rotatedMax[2]);

            shapes.add(Shapes.box(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ));
        });

        if (shapes.size() > 1) {
            return Shapes.or(shapes.get(0), shapes.subList(1, shapes.size()).toArray(new VoxelShape[0]));
        }
        return shapes.get(0);
    }

    public static double[] rotatePoint(double x, double y, double z, int angle, Direction.Axis axis) {
        return switch (axis) {
            case X ->
                // Rotate around X-axis
                    switch (angle) {
                        case 90 -> new double[]{x, z, 1.0D - y};
                        case 180 -> new double[]{x, 1.0D - y, 1.0D - z};
                        case 270 -> new double[]{x, 1.0D - z, y};
                        default -> new double[]{x, y, z};
                    };
            case Y ->
                // Rotate around Y-axis
                    switch (angle) {
                        case 90 -> new double[]{1.0D - z, y, x};
                        case 180 -> new double[]{1.0D - x, y, 1.0D - z};
                        case 270 -> new double[]{z, y, 1.0D - x};
                        default -> new double[]{x, y, z};
                    };
            case Z ->
                // Rotate around Z-axis
                    switch (angle) {
                        case 90 -> new double[]{y, 1.0D - x, z};
                        case 180 -> new double[]{1.0D - x, 1.0D - y, z};
                        case 270 -> new double[]{1.0D - y, x, z};
                        default -> new double[]{x, y, z};
                    };
            default -> throw new IllegalArgumentException("Invalid axis for rotation");
        };
    }

    public static Vec3 rotatePoint(Vec3 point, int angle, Direction.Axis axis) {
        double[] rotated = rotatePoint(point.x, point.y, point.z, angle, axis);
        return new Vec3(rotated[0], rotated[1], rotated[2]);
    }
}

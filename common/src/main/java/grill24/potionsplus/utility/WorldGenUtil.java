package grill24.potionsplus.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Random;
import java.util.function.Consumer;

public class WorldGenUtil {
    public static boolean[] generateLakeShape(
            RandomSource random,
            int numEllipsoids,
            double minRadiusX, double maxRadiusX,
            double minRadiusY, double maxRadiusY,
            double minRadiusZ, double maxRadiusZ,
            int width, int height, int depth,
            double xOffsetMin, double xOffsetMax,
            double yOffsetMin, double yOffsetMax,
            double zOffsetMin, double zOffsetMax) {

        boolean[] lakeShape = new boolean[width * depth * height];

        for (int i = 0; i < numEllipsoids; ++i) {
            double ellipsoidXRadius = random.nextDouble() * (maxRadiusX - minRadiusX) + minRadiusX;
            double ellipsoidYRadius = random.nextDouble() * (maxRadiusY - minRadiusY) + minRadiusY;
            double ellipsoidZRadius = random.nextDouble() * (maxRadiusZ - minRadiusZ) + minRadiusZ;

            double ellipsoidCenterX = random.nextDouble() * (width - ellipsoidXRadius - xOffsetMax) + xOffsetMin + ellipsoidXRadius / 2.0D;
            double ellipsoidCenterY = random.nextDouble() * (height - ellipsoidYRadius - yOffsetMax) + yOffsetMin + ellipsoidYRadius / 2.0D;
            double ellipsoidCenterZ = random.nextDouble() * (depth - ellipsoidZRadius - zOffsetMax) + zOffsetMin + ellipsoidZRadius / 2.0D;

            for (int x = 1; x < width - 1; ++x) {
                for (int z = 1; z < depth - 1; ++z) {
                    for (int y = 1; y < height - 1; ++y) {
                        double dx = ((double)x - ellipsoidCenterX) / (ellipsoidXRadius / 2.0D);
                        double dy = ((double)y - ellipsoidCenterY) / (ellipsoidYRadius / 2.0D);
                        double dz = ((double)z - ellipsoidCenterZ) / (ellipsoidZRadius / 2.0D);
                        double distanceSquared = dx * dx + dy * dy + dz * dz;
                        if (distanceSquared < 1.0D) {
                            lakeShape[(x * depth + z) * height + y] = true; // Mark this block as part of the lake
                        }
                    }
                }
            }
        }

        return lakeShape;
    }

    public static boolean[] generateRotatedEllipsoid(
            RandomSource random,
            double radiusX, double radiusY, double radiusZ,
            double azimuthalAngle, double polarAngle) {

        // Calculate the bounding box dimensions
        int width = (int) Math.ceil(2 * radiusX);
        int height = (int) Math.ceil(2 * radiusY);
        int depth = (int) Math.ceil(2 * radiusZ);

        // Calculate the center coordinates automatically
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double centerZ = depth / 2.0;

        boolean[] ellipsoidShape = new boolean[width * depth * height];

        // Precompute trigonometric values
        double cosAzimuth = Math.cos(azimuthalAngle);
        double sinAzimuth = Math.sin(azimuthalAngle);
        double cosPolar = Math.cos(polarAngle);
        double sinPolar = Math.sin(polarAngle);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < depth; ++z) {

                    // Translate the current point to the ellipsoid's local coordinate system
                    double translatedX = x - centerX;
                    double translatedY = y - centerY;
                    double translatedZ = z - centerZ;

                    // Apply rotation using spherical coordinates
                    double rotatedX = translatedX * cosAzimuth - translatedZ * sinAzimuth;
                    double rotatedZ = translatedX * sinAzimuth + translatedZ * cosAzimuth;

                    double finalX = rotatedX * cosPolar + translatedY * sinPolar;
                    double finalY = translatedY * cosPolar - rotatedX * sinPolar;

                    // Normalize to ellipsoid equation
                    double dx = finalX / radiusX;
                    double dy = finalY / radiusY;
                    double dz = rotatedZ / radiusZ;

                    // Check if the point is within the ellipsoid
                    if (dx * dx + dy * dy + dz * dz < 1.0) {
                        ellipsoidShape[(x * depth + z) * height + y] = true; // Mark this block as part of the ellipsoid
                    }
                }
            }
        }

        return ellipsoidShape;
    }

    public static void iterateEllipsoidShape(boolean[] ellipsoidShape, int width, int height, int depth, Consumer<BlockPos> consumer) {
        for (int index = 0; index < ellipsoidShape.length; index++) {
            if (ellipsoidShape[index]) {
                // Calculate x, y, z from the 1D index
                int y = index % height;
                int z = (index / height) % depth;
                int x = (index / (height * depth));

                // Now you have the coordinates (x, y, z)
                consumer.accept(new BlockPos(x, y, z));
            }
        }
    }


}

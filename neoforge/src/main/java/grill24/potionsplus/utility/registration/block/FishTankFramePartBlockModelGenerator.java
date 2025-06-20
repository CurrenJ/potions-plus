package grill24.potionsplus.utility.registration.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceBuilder;
import org.joml.Vector3i;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;

public class FishTankFramePartBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public static final TextureSlot FRAME = TextureSlot.create("frame");
    public static final TextureSlot GLASS = TextureSlot.create("glass");


    public FishTankFramePartBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {

    }

    public static ModelTemplate createModelTemplate(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> elementGenerators = new ArrayList<>();

        elementGenerators.addAll(generateSupports(faces));
        elementGenerators.addAll(generateFrameFaces(faces));
        elementGenerators.addAll(generateGlassPaneFaces(faces));

        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder().parent(mc("block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(FishTankFramePartBlockModelGenerator.FRAME)
                .requiredTextureSlot(FishTankFramePartBlockModelGenerator.GLASS)
                .requiredTextureSlot(TextureSlot.EDGE);
        elementGenerators.forEach(builder::element);
        return builder.build();
    }

    // Glass Pane Faces (For North, South, East, and West Directions)
    private static List<Consumer<ElementBuilder>> generateGlassPaneFaces(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> glassPaneFaceGenerators = new ArrayList<>();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!faces.getOrDefault(direction, true)) {
                continue; // Skip if the face is not present
            }
            glassPaneFaceGenerators.addAll(generateGlassPaneFace(direction, faces));
        }

        return glassPaneFaceGenerators;
    }

    private static final HashMap<Direction, Pair<Vector3i, Vector3i>> GLASS_PANES_POINTS = new HashMap<>() {{
        put(Direction.NORTH, new Pair<>(new Vector3i(1, 1, 0), new Vector3i(15, 15, 1)));
        put(Direction.SOUTH, new Pair<>(new Vector3i(1, 1, 15), new Vector3i(15, 15, 16)));
        put(Direction.EAST, new Pair<>(new Vector3i(15, 1, 1), new Vector3i(16, 15, 15)));
        put(Direction.WEST, new Pair<>(new Vector3i(0, 1, 1), new Vector3i(1, 15, 15)));
    }};

    private static List<Consumer<ElementBuilder>> generateGlassPaneFace(Direction direction, Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> glassPaneFaceGenerators = new ArrayList<>();

        Pair<Vector3i, Vector3i> points = GLASS_PANES_POINTS.get(direction);
        Vector3i fromOffset = new Vector3i(0, 0, 0);
        Vector3i toOffset = new Vector3i(0, 0, 0);
        if (!faces.getOrDefault(Direction.UP, false)) {
            toOffset.add(new Vector3i(0, 1, 0));
        }
        if (!faces.getOrDefault(Direction.DOWN, false)) {
            fromOffset.add(new Vector3i(0, -1, 0));
        }
        if (!faces.getOrDefault(Direction.NORTH, true) && direction != Direction.SOUTH) {
            fromOffset.add(new Vector3i(0, 0, -1));
        }
        if (!faces.getOrDefault(Direction.SOUTH, true) && direction != Direction.NORTH) {
            toOffset.add(new Vector3i(0, 0, 1));
        }
        if (!faces.getOrDefault(Direction.EAST, true) && direction != Direction.WEST) {
            toOffset.add(new Vector3i(1, 0, 0));
        }
        if (!faces.getOrDefault(Direction.WEST, true) && direction != Direction.EAST) {
            fromOffset.add(new Vector3i(-1, 0, 0));
        }
        points = new Pair<>(fromOffset.add(points.getA()), toOffset.add(points.getB()));

        Pair<Vector3i, Vector3i> finalPoints = points;
        glassPaneFaceGenerators.add(elementBuilder -> elementBuilder
                .from(finalPoints.getA().x, finalPoints.getA().y, finalPoints.getA().z)
                .to(finalPoints.getB().x, finalPoints.getB().y, finalPoints.getB().z)
                .textureAll(GLASS)
                .allFaces((faceDirection, faceBuilder) -> {
                    if (faceDirection == direction || faceDirection.getOpposite() == direction) {
                        faceBuilder.uvs(0F, 0F, 16F, 16F);
                    } else {
                        faceBuilder.texture(TextureSlot.EDGE);
                    }
                })
        );


        return glassPaneFaceGenerators;
    }

    private static Pair<Vector3i, Vector3i> extendInDirection(Pair<Vector3i, Vector3i> points, Direction direction, int length) {
        Vector3i start = points.getA();
        Vector3i end = points.getB();

        Vec3i dirVec = direction.getUnitVec3i();
        if (dirVec.getX() > 0 || dirVec.getY() > 0 || dirVec.getZ() > 0) {
            end = end.add(new Vector3i(dirVec.getX() * length, dirVec.getY() * length, dirVec.getZ() * length));
        } else if (dirVec.getX() < 0 || dirVec.getY() < 0 || dirVec.getZ() < 0) {
            start = start.add(new Vector3i(dirVec.getX() * length, dirVec.getY() * length, dirVec.getZ() * length));
        }

        return new Pair<>(start, end);
    }

    // Frame Faces (For Up and Down Directions)

    private static List<Consumer<ElementBuilder>> generateFrameFaces(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> frameFaceGenerators = new ArrayList<>();

        int xFrom = 0;
        int xTo = 16;
        int zFrom = 0;
        int zTo = 16;

        BiConsumer<Direction, FaceBuilder> faceUvGenerator = (direction, faceBuilder) -> {
            if (direction.getAxis() == Direction.Axis.Y) {
                faceBuilder.uvs(0F, 0F, 16F, 16F);
            } else {
                faceBuilder.uvs(0F, 0F, 16F, 1F);
            }
        };

        if (faces.getOrDefault(Direction.UP, true)) {
            frameFaceGenerators.add(elementBuilder -> elementBuilder
                    .from(xFrom, 15, zFrom)
                    .to(xTo, 16, zTo)
                    .textureAll(FRAME)
                    .allFaces(faceUvGenerator)
            );
        }

        if (faces.getOrDefault(Direction.DOWN, true)) {
            frameFaceGenerators.add(elementBuilder -> elementBuilder
                    .from(xFrom, 0, zFrom)
                    .to(xTo, 1, zTo)
                    .textureAll(FRAME)
                    .allFaces(faceUvGenerator)
            );
        }

        return frameFaceGenerators;
    }

    // ----- Corner Supports -----

    private static List<Consumer<ElementBuilder>> generateSupports(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> supportGenerators = new ArrayList<>();

        final Pair<Point, List<Direction>>[] supportPositions = new Pair[]{
                new Pair<>(new Point(0, 0), List.of(Direction.NORTH, Direction.WEST)), // adjacent to NORTH and WEST
                new Pair<>(new Point(0, 15), List.of(Direction.SOUTH, Direction.WEST)), // adjacent to SOUTH and WEST
                new Pair<>(new Point(15, 0), List.of(Direction.NORTH, Direction.EAST)), // adjacent to NORTH and EAST
                new Pair<>(new Point(15, 15), List.of(Direction.SOUTH, Direction.EAST)) // adjacent to SOUTH and EAST
        };
        final int yFrom = faces.getOrDefault(Direction.DOWN, false) ? 1 : 0;
        final int yTo = faces.getOrDefault(Direction.UP, false) ? 15 : 16;

        for (Pair<Point, List<Direction>> supportPosition : supportPositions) {
            boolean skip = false;
            for (Direction direction : supportPosition.getB()) {
                if (!faces.getOrDefault(direction, true)) {
                    skip = true;
                    break;
                }
            }

            if (skip) {
                continue; // Skip if any of the adjacent faces are not present
            }


            supportGenerators.add(elementBuilder -> generateSupport(elementBuilder, supportPosition.getA(), yFrom, yTo));
        }

        return supportGenerators;
    }

    private static Float[] getSupportUvs(Direction direction, int yFrom, int yTo) {
        switch (direction) {
            case NORTH, SOUTH, EAST, WEST -> {
                return new Float[]{0F, 0F, 1F, (float) (yTo - yFrom)};
            }
            case UP, DOWN -> {
                return new Float[]{0F, 0F, 1F, 1F};
            }
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        }
    }

    private static void generateSupport(ElementBuilder elementBuilder, Point position, int yFrom, int yTo) {
        elementBuilder
                .from(position.x, yFrom, position.y)
                .to(position.x + 1, yTo, position.y + 1)
                .textureAll(FRAME)
                .allFaces((direction, faceBuilder) -> {
                    Float[] uvs = getSupportUvs(direction, yFrom, yTo);
                    faceBuilder.uvs(uvs[0], uvs[1], uvs[2], uvs[3]);
                })
        ;
    }

    // ----- Faces -----
}

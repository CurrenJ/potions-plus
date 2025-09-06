package grill24.potionsplus.utility.registration.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;

public class FishTankSandPartBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public static final TextureSlot SAND = TextureSlot.create("sand");

    public FishTankSandPartBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {

    }

    public static ModelTemplate createModelTemplate(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> elementGenerators = new ArrayList<>();

        elementGenerators.addAll(generateSandFaces(faces));

        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder().parent(mc("block/block"))
                .requiredTextureSlot(FishTankSandPartBlockModelGenerator.SAND)
                .requiredTextureSlot(TextureSlot.PARTICLE);
        elementGenerators.forEach(builder::element);
        return builder.build();
    }

    private static List<Consumer<ElementBuilder>> generateSandFaces(Map<Direction, Boolean> faces) {
        List<Consumer<ElementBuilder>> sandFaceGenerators = new ArrayList<>();

        Vector3i from = new Vector3i(1, 1, 1);
        Vector3i to = new Vector3i(15, 2, 15);

        if (!faces.getOrDefault(Direction.DOWN, false)) {
            return sandFaceGenerators;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!faces.getOrDefault(direction, false)) {
                switch (direction) {
                    case NORTH -> {
                        from.add(0, 0, -1);
                    }
                    case SOUTH -> {
                        to.add(0, 0, 1);
                    }
                    case WEST -> {
                        from.add(-1, 0, 0);
                    }
                    case EAST -> {
                        to.add(1, 0, 0);
                    }
                }
            }
        }

        sandFaceGenerators.add(builder -> builder
                .from(from.x, from.y, from.z)
                .to(to.x, to.y, to.z)
                .textureAll(FishTankSandPartBlockModelGenerator.SAND)
        );

        return sandFaceGenerators;
    }
}

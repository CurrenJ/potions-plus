package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.GeneticCropBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class GeneticCropBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public record PlantTextures(Identifier texture, Identifier pollinatedTexture,
                                Identifier harvestableTexture) {
        public PlantTextures(Identifier texture) {
            this(texture, texture, texture);
        }

        public PlantTextures(Identifier texture, Identifier pollinatedTexture) {
            this(texture, pollinatedTexture, pollinatedTexture);
        }
    }

    public static final ModelTemplate CROP_CROSS = new ModelTemplate(
            Optional.of(ppId("block/crop_cross")),
            Optional.empty(),
            TextureSlot.CROP
    );

    private final PlantTextures[] ageTextures;
    private final ModelTemplate template;

    public GeneticCropBlockModelGenerator(Supplier<Holder<T>> blockGetter, ModelTemplate template, PlantTextures... ageTextures) {
        super(blockGetter);
        this.ageTextures = ageTextures;
        if (!template.requiredSlots.contains(TextureSlot.CROP) || template.requiredSlots.size() > 1) {
            throw new IllegalArgumentException("CropBlockModelGenerator requires a template with exactly one required texture slot: CROP");
        }
        this.template = template;
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        // TODO: Crop block model generation
        Block block = this.getHolder().value();
        if (block instanceof GeneticCropBlock && this.getHolder().key() != null) {
            Identifier baseModelLocation = this.getHolder().key().identifier();

            // Generate blockstate definition
            BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                    .with(
                            PropertyDispatch.initial(GeneticCropBlock.AGE, GeneticCropBlock.HARVESTABLE)
                                    .generate((age, harvestState) -> {
                                        Identifier modelLocation = Identifier.fromNamespaceAndPath(baseModelLocation.getNamespace(), baseModelLocation.getPath() + "_" + age + "_" + harvestState.getSerializedName());

                                        Identifier texture = getPlantTexture(age, harvestState);
                                        TextureMapping textureMapping = new TextureMapping()
                                                .put(TextureSlot.CROP, texture);

                                        template.create(modelLocation, textureMapping, blockModelGenerators.modelOutput);

                                        return BlockModelGenerators.plainVariant(modelLocation);
                                    })
                    );

            blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
        } else {
            throw new IllegalStateException("CropBlockModelGenerator can only be used for GeneticCropBlock, not " + block.getClass().getSimpleName() + "!");
        }
    }

    private Identifier getPlantTexture(Integer age, GeneticCropBlock.HarvestState harvestState) {
        PlantTextures textures = ageTextures[Math.clamp(age, 0, ageTextures.length - 1)];
        Identifier texture = switch (harvestState) {
            case GeneticCropBlock.HarvestState.IMMATURE -> textures.texture;
            case GeneticCropBlock.HarvestState.POLLINATED -> textures.pollinatedTexture;
            case GeneticCropBlock.HarvestState.MATURE -> textures.harvestableTexture;
        };
        return texture;
    }
}

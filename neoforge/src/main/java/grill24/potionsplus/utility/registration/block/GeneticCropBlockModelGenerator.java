package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.GeneticCropBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class GeneticCropBlockModelGenerator<T extends Block> extends BlockModelUtility.BlockModelGenerator<T> {
    public record PlantTextures(ResourceLocation texture, ResourceLocation pollinatedTexture,
                                ResourceLocation harvestableTexture) {
        public PlantTextures(ResourceLocation texture) {
            this(texture, texture, texture);
        }

        public PlantTextures(ResourceLocation texture, ResourceLocation pollinatedTexture) {
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
        if (block instanceof GeneticCropBlock && this.getHolder().getKey() != null) {
            ResourceLocation baseModelLocation = this.getHolder().getKey().location();
            ResourceLocation fallbackModelLocation = ResourceLocation.fromNamespaceAndPath(baseModelLocation.getNamespace(), baseModelLocation.getPath() + "_0");

            // Generate blockstate definition
            BlockModelDefinitionGenerator blockstateGenerator = MultiVariantGenerator.dispatch(block)
                    .with(
                            PropertyDispatch.initial(GeneticCropBlock.AGE, GeneticCropBlock.HARVESTABLE)
                                    .generate((age, harvestState) -> {
                                        ResourceLocation modelLocation = ResourceLocation.fromNamespaceAndPath(baseModelLocation.getNamespace(), baseModelLocation.getPath() + "_" + age + "_" + harvestState.getSerializedName());

                                        ResourceLocation texture = getPlantTexture(age, harvestState);
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

    private ResourceLocation getPlantTexture(Integer age, GeneticCropBlock.HarvestState harvestState) {
        PlantTextures textures = ageTextures[Math.clamp(age, 0, ageTextures.length - 1)];
        ResourceLocation texture = switch (harvestState) {
            case GeneticCropBlock.HarvestState.IMMATURE -> textures.texture;
            case GeneticCropBlock.HarvestState.POLLINATED -> textures.pollinatedTexture;
            case GeneticCropBlock.HarvestState.MATURE -> textures.harvestableTexture;
        };
        return texture;
    }
}

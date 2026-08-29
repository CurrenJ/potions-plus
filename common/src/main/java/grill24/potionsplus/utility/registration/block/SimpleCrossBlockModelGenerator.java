package grill24.potionsplus.utility.registration.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class SimpleCrossBlockModelGenerator<B extends Block> extends BlockModelUtility.BlockModelGenerator<B> {
    public SimpleCrossBlockModelGenerator(Supplier<Holder<B>> blockGetter) {
        super(blockGetter);
    }

    public static void registerFlowerBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block, Identifier texture) {
        // Generate block model
        Identifier flowerModelLocation = ModelTemplates.CROSS.create(block, new TextureMapping().put(TextureSlot.CROSS, new Material(texture)), blockModelGenerators.modelOutput);

        // Generate blockstate definition
        MultiVariantGenerator blockstateGenerator = BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(flowerModelLocation));
        blockModelGenerators.blockStateOutput.accept(blockstateGenerator);
    }

    public static void registerFlowerBlock(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Block block) {
        registerFlowerBlock(blockModelGenerators, itemModelGenerators, block, ModelLocationUtils.getModelLocation(block));
    }

    @Override
    public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
        registerFlowerBlock(blockModelGenerators, itemModelGenerators, getHolder().value());
    }
}

package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.block.VersatilePlantBlockTexturePattern;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.function.Supplier;

/**
 * Used to build versatile variants of 1 tall vanilla plants.
 */
public class SimpleVersatilePlantBlockBuilder extends SimpleBlockBuilder {
    public static <T> SimpleVersatilePlantBlockBuilder create(String name, Supplier<VersatilePlantBlockModelGenerator.ClientModelData<T>> clientModelDataSupplier) {
        SimpleVersatilePlantBlockBuilder builder = new SimpleVersatilePlantBlockBuilder();
        builder.name(name);
        builder.blockFactory(SimpleVersatilePlantBlockBuilder::createBlock);
        builder.modelGenerator(h -> new VersatilePlantBlockModelGenerator<>(h, clientModelDataSupplier.get()));
        builder.lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h));
        builder.renderType(RenderType.CUTOUT);
        return builder;
    }

    @Override
    protected SimpleVersatilePlantBlockBuilder self() {
        return this;
    }

    private static VersatilePlantBlock createBlock(BlockBehaviour.Properties prop) {
        return new VersatilePlantBlock(prop
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY),
                new VersatilePlantBlock.VersatilePlantConfig(
                        true,
                        false,
                        0, 0,
                        new VersatilePlantBlockTexturePattern(List.of(0), List.of(), List.of(), false)));
    }
}

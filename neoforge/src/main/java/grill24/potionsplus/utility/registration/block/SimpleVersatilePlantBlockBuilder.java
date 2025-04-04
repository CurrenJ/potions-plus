package grill24.potionsplus.utility.registration.block;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.block.VersatilePlantBlockTexturePattern;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.function.Function;

/**
 * Used to build versatile variants of 1 tall vanilla plants.
 */
public class SimpleVersatilePlantBlockBuilder extends SimpleBlockBuilder {
    public static <T> SimpleVersatilePlantBlockBuilder create(String name, T[] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, VersatilePlantBlockModelGenerator.IModelFactory<T> modelRegisterer) {
        SimpleVersatilePlantBlockBuilder builder = new SimpleVersatilePlantBlockBuilder();
        builder.name(name);
        builder.blockFactory(SimpleVersatilePlantBlockBuilder::createBlock);
        builder.modelGenerator(holder -> new VersatilePlantBlockModelGenerator<>(holder, resources, texRotationFunction, itemTexture, modelRegisterer));
        builder.lootGenerator(holder -> new BlockDropSelfLoot<>(LootContextParamSets.BLOCK, holder));
        return builder;
    }

    @Override
    protected SimpleVersatilePlantBlockBuilder self() {
        return this;
    }

    private static VersatilePlantBlock createBlock(BlockBehaviour.Properties prop) {
        return new VersatilePlantBlock(prop.mapColor(MapColor.PLANT)
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

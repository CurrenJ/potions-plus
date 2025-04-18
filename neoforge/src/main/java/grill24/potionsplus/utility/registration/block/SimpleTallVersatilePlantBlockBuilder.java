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

public class SimpleTallVersatilePlantBlockBuilder extends SimpleBlockBuilder {
    public static <T> SimpleTallVersatilePlantBlockBuilder create(String name, boolean extendable, T[] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, VersatilePlantBlockModelGenerator.IModelFactory<T> modelRegisterer) {
        SimpleTallVersatilePlantBlockBuilder builder = new SimpleTallVersatilePlantBlockBuilder();
        builder.name(name);
        builder.blockFactory(prop -> createBlock(prop, extendable));
        builder.modelGenerator(prop -> new VersatilePlantBlockModelGenerator<>(
                prop, resources, texRotationFunction, itemTexture, modelRegisterer));
        builder.lootGenerator(holder -> new BlockDropSelfLoot<>(LootContextParamSets.BLOCK, holder));
        builder.renderType(RenderType.CUTOUT);
        return builder;
    }

    @Override
    protected SimpleTallVersatilePlantBlockBuilder self() {
        return this;
    }

    private static VersatilePlantBlock createBlock(BlockBehaviour.Properties prop, boolean extendable) {
        return new VersatilePlantBlock(prop.mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY),
                new VersatilePlantBlock.VersatilePlantConfig(
                        true,
                        false,
                        1, extendable ? 5 : 1,
                        new VersatilePlantBlockTexturePattern(List.of(0), List.of(0), List.of(1), false)));
    }
}

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

public class SimpleTallVersatilePlantBlockBuilder extends SimpleBlockBuilder {
    public static <T> SimpleTallVersatilePlantBlockBuilder create(String name, boolean extendable, Supplier<VersatilePlantBlockModelGenerator.ClientModelData<T>> clientModelDataSupplier) {
        SimpleTallVersatilePlantBlockBuilder builder = new SimpleTallVersatilePlantBlockBuilder();
        builder.name(name);
        builder.blockFactory(prop -> createBlock(prop, extendable));
        builder.modelGenerator(prop -> new VersatilePlantBlockModelGenerator<>(prop, clientModelDataSupplier.get()));
        builder.lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h));
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

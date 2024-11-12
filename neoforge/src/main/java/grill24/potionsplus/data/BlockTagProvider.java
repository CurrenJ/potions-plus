package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.BREWING_CAULDRON.value(), Blocks.ABYSSAL_TROVE.value(), Blocks.SANGUINE_ALTAR.value(), Blocks.PRECISION_DISPENSER.value(), Blocks.PARTICLE_EMITTER.value());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Blocks.ABYSSAL_TROVE.value());
        tag(BlockTags.MINEABLE_WITH_AXE).add(Blocks.CLOTHESLINE.value(), Blocks.HERBALISTS_LECTERN.value());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), Blocks.REMNANT_DEBRIS.value(), Blocks.DEEPSLATE_REMNANT_DEBRIS.value());
        tag(BlockTags.DIAMOND_ORES).add(Blocks.DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.URANIUM_ORE.value(), Blocks.DEEPSLATE_URANIUM_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.SULFURIC_NETHER_QUARTZ_ORE.value());
        tag(Tags.Blocks.ORES).add(Blocks.DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_REMNANT_DEBRIS.value(), Blocks.REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_DIAMOND).add(Blocks.DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(Blocks.DENSE_DIAMOND_ORE.value(), Blocks.REMNANT_DEBRIS.value());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value(), Blocks.DEEPSLATE_REMNANT_DEBRIS.value());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Blocks.SANDY_COPPER_ORE.value(), Blocks.SANDY_IRON_ORE.value(), Blocks.SANDY_GOLD_ORE.value(), Blocks.SANDY_DIAMOND_ORE.value(), Blocks.SANDY_EMERALD_ORE.value(), Blocks.SANDY_LAPIS_ORE.value(), Blocks.SANDY_REDSTONE_ORE.value(), Blocks.SANDY_COAL_ORE.value(), Blocks.SANDY_URANIUM_ORE.value());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.MOSSY_COPPER_ORE.value(), Blocks.MOSSY_IRON_ORE.value(), Blocks.MOSSY_GOLD_ORE.value(), Blocks.MOSSY_DIAMOND_ORE.value(), Blocks.MOSSY_EMERALD_ORE.value(), Blocks.MOSSY_LAPIS_ORE.value(), Blocks.MOSSY_REDSTONE_ORE.value(), Blocks.MOSSY_COAL_ORE.value(), Blocks.MOSSY_URANIUM_ORE.value());

        tag(grill24.potionsplus.core.Tags.Blocks.FREEZABLE).add(net.minecraft.world.level.block.Blocks.WATER);

        tag(grill24.potionsplus.core.Tags.Blocks.CAVE_REPLACEABLE)
                .addTag(BlockTags.MOSS_REPLACEABLE)
                .add(net.minecraft.world.level.block.Blocks.COAL_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE,
                        net.minecraft.world.level.block.Blocks.IRON_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE,
                        net.minecraft.world.level.block.Blocks.COPPER_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE,
                        net.minecraft.world.level.block.Blocks.GOLD_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE,
                        net.minecraft.world.level.block.Blocks.DIAMOND_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE,
                        net.minecraft.world.level.block.Blocks.EMERALD_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_EMERALD_ORE,
                        net.minecraft.world.level.block.Blocks.LAPIS_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE,
                        net.minecraft.world.level.block.Blocks.REDSTONE_ORE,
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE,
                        Blocks.URANIUM_ORE.value(),
                        Blocks.DEEPSLATE_URANIUM_ORE.value()
                        );
    }

    @Override
    public String getName() {
        return "Potions Plus block tags";
    }
}

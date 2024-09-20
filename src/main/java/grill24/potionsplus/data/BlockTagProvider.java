package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.BREWING_CAULDRON.get(), Blocks.ABYSSAL_TROVE.get(), Blocks.SANGUINE_ALTAR.get(), Blocks.PRECISION_DISPENSER.get(), Blocks.PARTICLE_EMITTER.get());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Blocks.ABYSSAL_TROVE.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(Blocks.CLOTHESLINE.get(), Blocks.HERBALISTS_LECTERN.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(BlockTags.DIAMOND_ORES).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_DIAMOND).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(Blocks.DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Blocks.SANDY_COPPER_ORE.get(), Blocks.SANDY_IRON_ORE.get(), Blocks.SANDY_GOLD_ORE.get(), Blocks.SANDY_DIAMOND_ORE.get(), Blocks.SANDY_EMERALD_ORE.get(), Blocks.SANDY_LAPIS_ORE.get(), Blocks.SANDY_REDSTONE_ORE.get(), Blocks.SANDY_COAL_ORE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.MOSSY_COPPER_ORE.get(), Blocks.MOSSY_IRON_ORE.get(), Blocks.MOSSY_GOLD_ORE.get(), Blocks.MOSSY_DIAMOND_ORE.get(), Blocks.MOSSY_EMERALD_ORE.get(), Blocks.MOSSY_LAPIS_ORE.get(), Blocks.MOSSY_REDSTONE_ORE.get(), Blocks.MOSSY_COAL_ORE.get());

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
                        net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE
                        );

//        tag(Tags.Blocks.GEODE_GRACE_SPAWNABLE).add(Blocks.COAL_ORE);
//        tag(Tags.Blocks.GEODE_GRACE_SPAWNABLE).add(Blocks.COPPER_ORE);
//        tag(Tags.Blocks.GEODE_GRACE_SPAWNABLE).add(Blocks.IRON_ORE);
//        tag(Tags.Blocks.GEODE_GRACE_SPAWNABLE).add(Blocks.GOLD_ORE);
//        tag(Tags.Blocks.GEODE_GRACE_SPAWNABLE).add(Blocks.DIAMOND_ORE);
    }

    @Override
    public String getName() {
        return "Potions Plus block tags";
    }
}

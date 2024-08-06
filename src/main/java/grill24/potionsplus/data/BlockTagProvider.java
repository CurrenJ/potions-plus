package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagProvider extends BlockTagsProvider {


    public BlockTagProvider(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.BREWING_CAULDRON.get(), Blocks.ABYSSAL_TROVE.get(), Blocks.SANGUINE_ALTAR.get(), Blocks.PRECISION_DISPENSER.get(), Blocks.PARTICLE_EMITTER.get());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Blocks.ABYSSAL_TROVE.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(Blocks.CLOTHESLINE.get(), Blocks.HERBALISTS_LECTERN.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(BlockTags.DIAMOND_ORES).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_DIAMOND).add(Blocks.DENSE_DIAMOND_ORE.get(), Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(Blocks.DENSE_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());

        tag(grill24.potionsplus.core.Tags.Blocks.FREEZABLE).add(net.minecraft.world.level.block.Blocks.WATER);

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

package grill24.potionsplus.data;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagProvider extends TagsProvider<Block> {
    protected BlockTagProvider(DataGenerator dataGenerator, Registry<Block> blockRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockRegistry, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
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

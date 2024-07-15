package grill24.potionsplus.data;

import grill24.potionsplus.core.Tags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagProvider extends ItemTagsProvider {


    public ItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(grill24.potionsplus.core.Tags.Items.POTION_AMPLIFIER_UP_INGREDIENT_POOL).add(
                net.minecraft.world.item.Items.GLOWSTONE_DUST,
                net.minecraft.world.item.Items.REDSTONE,
                net.minecraft.world.item.Items.SUGAR,
                net.minecraft.world.item.Items.GUNPOWDER);

        tag(Tags.Items.BASE_TIER_POTION_INGREDIENTS).add(
                Items.STICK,
                Items.EGG,
                Items.SUGAR,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.SWEET_BERRIES,
                Items.CHARCOAL,
                Items.ROTTEN_FLESH,
                Items.BONE,
                Items.STRING,
                Items.FEATHER,
                Items.LEATHER,
                Items.ALLIUM,
                Items.AZURE_BLUET,
                Items.BLUE_ORCHID,
                Items.CORNFLOWER,
                Items.DANDELION,
                Items.LILAC,
                Items.LILY_OF_THE_VALLEY,
                Items.ORANGE_TULIP,
                Items.OXEYE_DAISY,
                Items.WARPED_FUNGUS,
                Items.CRIMSON_FUNGUS,
                Items.BAMBOO,
                Items.CACTUS
        );


        tag(Tags.Items.TIER_1_POTION_INGREDIENTS).add(
                Items.HONEYCOMB,
                Items.MUSHROOM_STEW,
                Items.GOLDEN_CARROT,
                Items.BEETROOT_SOUP,
                Items.SPIDER_EYE,
                Items.PUFFERFISH,
                Items.APPLE,
                Items.SALMON,
                Items.HONEY_BOTTLE,
                Items.GLOW_BERRIES,
                Items.SEA_PICKLE,
                Items.AMETHYST_SHARD,
                Items.SLIME_BALL
        );

        tag(Tags.Items.TIER_2_POTION_INGREDIENTS).add(
                Items.ENDER_PEARL,
                Items.GHAST_TEAR,
                Items.BLAZE_POWDER,
                Items.MAGMA_CREAM,
                Items.DIAMOND,
                Items.AMETHYST_CLUSTER,
                Items.RABBIT_FOOT,
                Items.GOLDEN_APPLE,
                Items.TROPICAL_FISH,
                Items.TURTLE_EGG,
                Items.CHORUS_FLOWER,
                Items.SCUTE,
                Items.FIRE_CORAL,
                Items.TUBE_CORAL,
                Items.POISONOUS_POTATO,
                Items.CAKE
        );

        tag(Tags.Items.TIER_3_POTION_INGREDIENTS).add(
                Items.NETHER_STAR,
                Items.TOTEM_OF_UNDYING,
                Items.WITHER_ROSE,
                Items.SHULKER_SHELL,
                Items.NAUTILUS_SHELL,
                Items.HEART_OF_THE_SEA,
                Items.END_CRYSTAL,
                Items.DRAGON_HEAD,
                Items.CREEPER_HEAD
        );

        tag(Tags.Items.GEODE_GRACE_BASE_TIER_INGREDIENTS).addTag(Tags.Items.BASE_TIER_POTION_INGREDIENTS).add(
                Items.NETHER_GOLD_ORE,
                Items.GOLD_ORE,
                Items.IRON_ORE,
                Items.COAL_ORE,
                Items.LAPIS_ORE,
                Items.NETHER_QUARTZ_ORE,
                Items.COPPER_ORE
        );

        tag(Tags.Items.GEODE_GRACE_TIER_1_INGREDIENTS).addTag(Tags.Items.TIER_1_POTION_INGREDIENTS).add(
                Items.LAPIS_LAZULI,
                Items.REDSTONE_ORE,
                Items.NETHER_GOLD_ORE,
                Items.COPPER_ORE
        );
    }

    @Override
    public String getName() {
        return "Potions Plus item tags";
    }
}

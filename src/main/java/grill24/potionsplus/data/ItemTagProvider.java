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
                Items.LEATHER
        );

        tag(Tags.Items.TIER_1_POTION_INGREDIENTS).add(
                Items.GLOWSTONE_DUST,
                Items.REDSTONE,
                Items.HONEYCOMB,
                Items.MUSHROOM_STEW,
                Items.GOLDEN_CARROT,
                Items.BEETROOT_SOUP,
                Items.SPIDER_EYE,
                Items.PUFFERFISH,
                Items.APPLE,
                Items.SALMON,
                Items.HONEY_BOTTLE,
                Items.GLOW_BERRIES
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
                Items.TURTLE_EGG
        );

        tag(Tags.Items.TIER_3_POTION_INGREDIENTS).add(
                Items.NETHER_STAR,
                Items.TOTEM_OF_UNDYING,
                Items.WITHER_ROSE,
                Items.SHULKER_SHELL,
                Items.NAUTILUS_SHELL,
                Items.HEART_OF_THE_SEA,
                Items.END_CRYSTAL,
                Items.DRAGON_HEAD
        );
    }

    @Override
    public String getName() {
        return "Potions Plus item tags";
    }
}

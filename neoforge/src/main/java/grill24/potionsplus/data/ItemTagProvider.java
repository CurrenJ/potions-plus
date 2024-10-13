package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        common();
        rare();

        tag(Tags.Items.FOOD_INGREDIENTS_COMMON).add(
                Items.APPLE,
                Items.BAKED_POTATO,
                Items.BREAD,
                Items.CARROT,
                Items.COOKED_BEEF,
                Items.COOKED_CHICKEN,
                Items.COOKED_COD,
                Items.COOKED_MUTTON,
                Items.COOKED_PORKCHOP,
                Items.COOKED_RABBIT,
                Items.COOKED_SALMON,
                Items.MELON_SLICE,
                Items.MUSHROOM_STEW,
                Items.PUMPKIN_PIE,
                Items.RABBIT_STEW,
                Items.SWEET_BERRIES,
                Items.BEETROOT_SOUP,
                Items.DRIED_KELP
        );

        tag(Tags.Items.FOOD_INGREDIENTS_UNCOMMON).add(
                Items.BEETROOT,
                Items.COOKED_COD,
                Items.COOKED_SALMON,
                Items.GOLDEN_APPLE,
                Items.GOLDEN_CARROT,
                Items.HONEYCOMB,
                Items.MILK_BUCKET,
                Items.SPIDER_EYE,
                Items.SUSPICIOUS_STEW,
                Items.TROPICAL_FISH,
                Items.HONEY_BOTTLE,
                Items.COOKIE
        );

        tag(Tags.Items.FOOD_INGREDIENTS_RARE).add(
                Items.GLOW_BERRIES,
                Items.PUFFERFISH,
                Items.TROPICAL_FISH,
                Items.GOLDEN_APPLE,
                Items.GOLDEN_CARROT,
                Items.CAKE,
                Items.POPPED_CHORUS_FRUIT
        );

        tag(Tags.Items.POTION_AMPLIFIER_UP_INGREDIENTS).add(
                Items.SADDLE,
                Items.ARMADILLO_SCUTE,
                grill24.potionsplus.core.Items.MOSS.value(),
                grill24.potionsplus.core.Items.WORMROOT.value(),
                grill24.potionsplus.core.Items.ROTTEN_WORMROOT.value(),
                grill24.potionsplus.core.Items.LUNAR_BERRIES.value(),
                grill24.potionsplus.core.Items.SALT.value(),
                Items.GOLD_BLOCK,
                Items.DIAMOND,
                Items.GHAST_TEAR
        );

        tag(Tags.Items.POTION_DURATION_UP_INGREDIENTS).add(
                grill24.potionsplus.core.Items.MOSS.value(),
                grill24.potionsplus.core.Items.LUNAR_BERRIES.value(),
                grill24.potionsplus.core.Items.SALT.value(),
                grill24.potionsplus.core.Items.ROTTEN_WORMROOT.value(),
                Items.AMETHYST_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.LAPIS_BLOCK,
                Items.GHAST_TEAR
        );

        tag(Tags.Items.ORE_FLOWERS_COMMON).add(
                Blocks.IRON_OXIDE_DAISY.value().asItem(),
                Blocks.COPPER_CHRYSANTHEMUM.value().asItem(),
                Blocks.BLACK_COALLA_LILY.value().asItem(),
                Blocks.REDSTONE_ROSE.value().asItem()
        );

        tag(Tags.Items.ORE_FLOWERS_RARE).add(
                Blocks.DIAMOUR.value().asItem(),
                Blocks.GOLDEN_CUBENSIS.value().asItem(),
                Blocks.LAPIS_LILAC.value().asItem()
        );
    }

    @Override
    public String getName() {
        return "Potions Plus item tags";
    }

    public void common() {
        tag(Tags.Items.COMMON_INGREDIENTS).add(
                Items.KELP,
                Items.BAMBOO,
                Items.CACTUS,
                Items.PUMPKIN,
                Items.LILY_PAD,
                Items.GLOW_LICHEN,
                Items.LARGE_FERN,
                Items.WHEAT_SEEDS,
                Items.MELON_SEEDS,
                Items.PUMPKIN_SEEDS,
                Items.CHARCOAL,
                Items.SWEET_BERRIES,
                Items.GLOW_BERRIES,
                Items.RED_MUSHROOM,
                Items.BROWN_MUSHROOM,
                Items.SUGAR_CANE,
                Items.COD,
                Items.SALMON,
                Items.ROTTEN_FLESH,
                Items.BONE,
                Items.PORKCHOP,
                Items.CHICKEN,
                Items.BEEF,
                Items.DEAD_BUSH,
                Items.FLOWERING_AZALEA,
                Items.HANGING_ROOTS,
                Items.VINE,
                Items.TROPICAL_FISH,
                Items.FEATHER,
                Items.APPLE,
                Items.EGG,
                Items.SLIME_BALL,
                Items.HONEYCOMB,
                Items.BEETROOT_SEEDS,
                Items.SEA_PICKLE,
                Items.WARPED_FUNGUS,
                Items.CRIMSON_FUNGUS
        );
    }

    public void rare() {
        tag(Tags.Items.RARE_INGREDIENTS).add(
                Items.PUFFERFISH,
                Items.CHORUS_FLOWER,
                Items.MELON,
                Items.SPORE_BLOSSOM,
                Items.TURTLE_SCUTE,
                Items.TURTLE_EGG,
                Items.AMETHYST_SHARD,
                Items.CAKE,
                Items.HONEY_BOTTLE,
                Items.POISONOUS_POTATO,
                Items.PHANTOM_MEMBRANE,
                Items.SPONGE,
                Items.BROWN_MUSHROOM_BLOCK,
                Items.RED_MUSHROOM_BLOCK,
                Items.GOLDEN_APPLE,
                Items.AMETHYST_CLUSTER,
                Items.SHULKER_SHELL,
                Items.NAUTILUS_SHELL,
                Items.TOTEM_OF_UNDYING
        );
    }
}

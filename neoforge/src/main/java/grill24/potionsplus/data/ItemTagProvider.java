package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
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

        tag(Tags.Items.REMNANT_DEBRIS).add(
                grill24.potionsplus.core.Items.REMNANT_DEBRIS.value(),
                grill24.potionsplus.core.Items.DEEPSLATE_REMNANT_DEBRIS.value()
        );

        tag(Tags.Items.URANIUM_ORE).add(
                Blocks.URANIUM_ORE.value().asItem(),
                Blocks.DEEPSLATE_URANIUM_ORE.value().asItem(),
                Blocks.SANDY_URANIUM_ORE.value().asItem(),
                Blocks.MOSSY_URANIUM_ORE.value().asItem()
        );

        tag(Tags.Items.EDIBLE_REWARDS).add(
                grill24.potionsplus.core.Items.SPARKLING_SQUASH.getItem(),
                grill24.potionsplus.core.Items.BLUEB_BERRIES.getItem(),
                grill24.potionsplus.core.Items.FORTIFYING_FUDGE.getItem(),
                grill24.potionsplus.core.Items.GRASS_CLIPPINGS.getItem(),
                grill24.potionsplus.core.Items.STONE_FRUIT.getItem(),
                grill24.potionsplus.core.Items.CHOCOLATE_BOOK.getItem(),
                grill24.potionsplus.core.Items.ROASTED_BAMBOO.getItem(),
                grill24.potionsplus.core.Items.MOSSASHIMI.getItem(),
                grill24.potionsplus.core.Items.PYRAMIDS_OF_SALT.getItem(),
                grill24.potionsplus.core.Items.BASIC_LOOT.getItem(),
                grill24.potionsplus.core.Items.INTERMEDIATE_LOOT.getItem(),
                grill24.potionsplus.core.Items.ADVANCED_LOOT.getItem(),
                grill24.potionsplus.core.Items.MASTER_LOOT.getItem(),
                grill24.potionsplus.core.Items.WHEEL.getItem()
        );

        tag(ItemTags.FISHING_ENCHANTABLE).add(grill24.potionsplus.core.Items.COPPER_FISHING_ROD.value());

        tag(Tags.Items.PP_FISH).add(
                grill24.potionsplus.core.Items.NORTHERN_PIKE.getItem(),
                grill24.potionsplus.core.Items.PARROTFISH.getItem(),
                grill24.potionsplus.core.Items.RAINFORDIA.getItem(),
                grill24.potionsplus.core.Items.GARDEN_EEL.getItem(),
                grill24.potionsplus.core.Items.ROYAL_GARDEN_EEL.getItem(),
                grill24.potionsplus.core.Items.LONGNOSE_GAR.getItem(),
                grill24.potionsplus.core.Items.SHRIMP.getItem(),
                grill24.potionsplus.core.Items.FRIED_SHRIMP.getItem(),
                grill24.potionsplus.core.Items.MOORISH_IDOL.getItem(),
                grill24.potionsplus.core.Items.MOLTEN_MOORISH_IDOL.getItem(),
                grill24.potionsplus.core.Items.OCEAN_SUNFISH.getItem(),
                grill24.potionsplus.core.Items.PORTUGUESE_MAN_O_WAR.getItem(),
                grill24.potionsplus.core.Items.BLUEGILL.getItem(),
                grill24.potionsplus.core.Items.NEON_TETRA.getItem(),
                grill24.potionsplus.core.Items.GIANT_MANTA_RAY.getItem(),
                grill24.potionsplus.core.Items.FROZEN_GIANT_MANTA_RAY.getItem(),
                grill24.potionsplus.core.Items.LIZARDFISH.getItem()
        );
        tag(ItemTags.FISHES).addTag(Tags.Items.PP_FISH);

        tag(Tags.Items.PP_FISHING_COPPER_FRAME).add(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH,
                Items.PUFFERFISH
        );
        tag(Tags.Items.PP_FISHING_GOLD_FRAME).add(
                grill24.potionsplus.core.Items.PARROTFISH.getItem(),
                grill24.potionsplus.core.Items.SHRIMP.getItem(),
                grill24.potionsplus.core.Items.OCEAN_SUNFISH.getItem(),
                grill24.potionsplus.core.Items.NORTHERN_PIKE.getItem(),
                grill24.potionsplus.core.Items.RAINFORDIA.getItem(),
                grill24.potionsplus.core.Items.LONGNOSE_GAR.getItem(),
                grill24.potionsplus.core.Items.BLUEGILL.getItem(),
                grill24.potionsplus.core.Items.NEON_TETRA.getItem()
        );
        tag(Tags.Items.PP_FISHING_DIAMOND_FRAME).add(
                grill24.potionsplus.core.Items.GARDEN_EEL.getItem(),
                grill24.potionsplus.core.Items.GIANT_MANTA_RAY.getItem(),
                grill24.potionsplus.core.Items.MOORISH_IDOL.getItem(),
                grill24.potionsplus.core.Items.PORTUGUESE_MAN_O_WAR.getItem(),
                grill24.potionsplus.core.Items.LIZARDFISH.getItem()
        );
        tag(Tags.Items.PP_FISHING_PURPLE_FRAME).add(
                grill24.potionsplus.core.Items.MOLTEN_MOORISH_IDOL.getItem(),
                grill24.potionsplus.core.Items.FROZEN_GIANT_MANTA_RAY.getItem(),
                grill24.potionsplus.core.Items.ROYAL_GARDEN_EEL.getItem(),
                grill24.potionsplus.core.Items.FRIED_SHRIMP.getItem()
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
                Items.NAUTILUS_SHELL
        );
    }
}

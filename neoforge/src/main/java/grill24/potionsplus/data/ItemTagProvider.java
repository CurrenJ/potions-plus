package grill24.potionsplus.data;

import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.core.items.SkillLootItems;
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
                BrewingItems.MOSS.value(),
                BrewingItems.WORMROOT.value(),
                BrewingItems.ROTTEN_WORMROOT.value(),
                BrewingItems.LUNAR_BERRIES.value(),
                BrewingItems.SALT.value(),
                Items.GOLD_BLOCK,
                Items.DIAMOND,
                Items.GHAST_TEAR
        );

        tag(Tags.Items.POTION_DURATION_UP_INGREDIENTS).add(
                BrewingItems.MOSS.value(),
                BrewingItems.LUNAR_BERRIES.value(),
                BrewingItems.SALT.value(),
                BrewingItems.ROTTEN_WORMROOT.value(),
                Items.AMETHYST_BLOCK,
                Items.REDSTONE_BLOCK,
                Items.LAPIS_BLOCK,
                Items.GHAST_TEAR
        );

        tag(Tags.Items.ORE_FLOWERS_COMMON).add(
                FlowerBlocks.IRON_OXIDE_DAISY.value().asItem(),
                FlowerBlocks.COPPER_CHRYSANTHEMUM.value().asItem(),
                FlowerBlocks.BLACK_COALLA_LILY.value().asItem(),
                FlowerBlocks.REDSTONE_ROSE.value().asItem()
        );

        tag(Tags.Items.ORE_FLOWERS_RARE).add(
                FlowerBlocks.DIAMOUR.value().asItem(),
                FlowerBlocks.GOLDEN_CUBENSIS.value().asItem(),
                FlowerBlocks.LAPIS_LILAC.value().asItem()
        );

        tag(Tags.Items.REMNANT_DEBRIS).add(
                OreBlocks.REMNANT_DEBRIS.value().asItem(),
                OreBlocks.DEEPSLATE_REMNANT_DEBRIS.value().asItem()
        );

        tag(Tags.Items.URANIUM_ORE).add(
                OreBlocks.URANIUM_ORE.value().asItem(),
                OreBlocks.DEEPSLATE_URANIUM_ORE.value().asItem(),
                OreBlocks.SANDY_URANIUM_ORE.value().asItem(),
                OreBlocks.STONEY_URANIUM_ORE.value().asItem()
        );

        tag(Tags.Items.EDIBLE_REWARDS).add(
                SkillLootItems.SPARKLING_SQUASH.getValue(),
                SkillLootItems.BLUEB_BERRIES.getValue(),
                SkillLootItems.FORTIFYING_FUDGE.getValue(),
                SkillLootItems.GRASS_CLIPPINGS.getValue(),
                SkillLootItems.STONE_FRUIT.getValue(),
                SkillLootItems.CHOCOLATE_BOOK.getValue(),
                SkillLootItems.ROASTED_BAMBOO.getValue(),
                SkillLootItems.MOSSASHIMI.getValue(),
                SkillLootItems.PYRAMIDS_OF_SALT.getValue(),
                SkillLootItems.BASIC_LOOT.getValue(),
                SkillLootItems.INTERMEDIATE_LOOT.getValue(),
                SkillLootItems.ADVANCED_LOOT.getValue(),
                SkillLootItems.MASTER_LOOT.getValue(),
                SkillLootItems.WHEEL.getValue()
        );

        tag(ItemTags.FISHING_ENCHANTABLE).add(FishItems.COPPER_FISHING_ROD.value()).add(FishItems.OBSIDIAN_FISHING_ROD.value());

        tag(Tags.Items.PP_FISH).add(
                FishItems.NORTHERN_PIKE.getValue(),
                FishItems.PARROTFISH.getValue(),
                FishItems.RAINFORDIA.getValue(),
                FishItems.GARDEN_EEL.getValue(),
                FishItems.ROYAL_GARDEN_EEL.getValue(),
                FishItems.LONGNOSE_GAR.getValue(),
                FishItems.SHRIMP.getValue(),
                FishItems.FRIED_SHRIMP.getValue(),
                FishItems.MOORISH_IDOL.getValue(),
                FishItems.MOLTEN_MOORISH_IDOL.getValue(),
                FishItems.OCEAN_SUNFISH.getValue(),
                FishItems.PORTUGUESE_MAN_O_WAR.getValue(),
                FishItems.BLUEGILL.getValue(),
                FishItems.NEON_TETRA.getValue(),
                FishItems.GIANT_MANTA_RAY.getValue(),
                FishItems.FROZEN_GIANT_MANTA_RAY.getValue(),
                FishItems.LIZARDFISH.getValue()
        );

        tag(Tags.Items.BAIT).add(FishItems.WORMS.getValue(), FishItems.GUMMY_WORMS.getValue(), FishItems.BLAZED_GRUB.getValue());
        tag(ItemTags.FISHES).addTag(Tags.Items.PP_FISH);
        tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH).addTag(Tags.Items.PP_FISH);

        tag(Tags.Items.PP_FISHING_COPPER_FRAME).add(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH,
                Items.PUFFERFISH,
                FishItems.WORMS.getValue(),
                FishItems.BLAZED_GRUB.getValue()
        );
        tag(Tags.Items.PP_FISHING_GOLD_FRAME).add(
                FishItems.PARROTFISH.getValue(),
                FishItems.SHRIMP.getValue(),
                FishItems.OCEAN_SUNFISH.getValue(),
                FishItems.NORTHERN_PIKE.getValue(),
                FishItems.RAINFORDIA.getValue(),
                FishItems.LONGNOSE_GAR.getValue(),
                FishItems.BLUEGILL.getValue(),
                FishItems.NEON_TETRA.getValue(),
                FishItems.GUMMY_WORMS.getValue()
        );
        tag(Tags.Items.PP_FISHING_DIAMOND_FRAME).add(
                FishItems.GARDEN_EEL.getValue(),
                FishItems.GIANT_MANTA_RAY.getValue(),
                FishItems.MOORISH_IDOL.getValue(),
                FishItems.PORTUGUESE_MAN_O_WAR.getValue(),
                FishItems.LIZARDFISH.getValue()
        );
        tag(Tags.Items.PP_FISHING_PURPLE_FRAME).add(
                FishItems.MOLTEN_MOORISH_IDOL.getValue(),
                FishItems.FROZEN_GIANT_MANTA_RAY.getValue(),
                FishItems.ROYAL_GARDEN_EEL.getValue(),
                FishItems.FRIED_SHRIMP.getValue()
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

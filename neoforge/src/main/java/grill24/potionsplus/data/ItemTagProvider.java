package grill24.potionsplus.data;

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
        tag(grill24.potionsplus.core.Tags.Items.POTION_AMPLIFIER_UP_INGREDIENT_POOL).add(
                net.minecraft.world.item.Items.GLOWSTONE_DUST,
                net.minecraft.world.item.Items.REDSTONE,
                net.minecraft.world.item.Items.SUGAR,
                net.minecraft.world.item.Items.GUNPOWDER);

        tier0();
        tier1();
        tier2();
        tier3();

        tag(Tags.Items.GEODE_GRACE_BASE_TIER_INGREDIENTS).addTag(Tags.Items.BASE_TIER_POTION_INGREDIENTS).add(
                grill24.potionsplus.core.Blocks.IRON_OXIDE_DAISY.value().asItem(),
                grill24.potionsplus.core.Blocks.COPPER_CHRYSANTHEMUM.value().asItem(),
                grill24.potionsplus.core.Blocks.LAPIS_LILAC.value().asItem(),
                grill24.potionsplus.core.Blocks.REDSTONE_ROSE.value().asItem()
        );

        tag(Tags.Items.GEODE_GRACE_TIER_1_INGREDIENTS).addTag(Tags.Items.TIER_1_POTION_INGREDIENTS).add(
                grill24.potionsplus.core.Blocks.DIAMOUR.value().asItem(),
                grill24.potionsplus.core.Blocks.GOLDEN_CUBENSIS.value().asItem(),
                grill24.potionsplus.core.Blocks.BLACK_COALLA_LILY.value().asItem()
        );

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
                Items.DRIED_KELP,
                grill24.potionsplus.core.Items.MOSS.value(),
                grill24.potionsplus.core.Items.WORMROOT.value()
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
                Items.COOKIE,
                grill24.potionsplus.core.Items.LUNAR_BERRIES.value(),
                grill24.potionsplus.core.Items.SALT.value(),
                grill24.potionsplus.core.Items.ROTTEN_WORMROOT.value()
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
    }

    @Override
    public String getName() {
        return "Potions Plus item tags";
    }

    public void tier0() {
        tag(Tags.Items.BASE_TIER_POTION_INGREDIENTS).add(
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
                Items.BEEF
        );
    }

    public void tier1() {
        tag(Tags.Items.TIER_1_POTION_INGREDIENTS).add(
                Items.DEAD_BUSH,
                Items.FLOWERING_AZALEA,
                Items.HANGING_ROOTS,
                Items.VINE,
                Items.MUSHROOM_STEM,
                Items.TROPICAL_FISH,
                Items.FEATHER,
                Items.LILY_PAD,
                Items.APPLE,
                Items.MUSHROOM_STEM,
                Items.EGG,
                Items.SLIME_BALL,
                Items.HONEYCOMB,
                Items.BEETROOT_SEEDS,
                Items.SEA_PICKLE,
                Items.CHICKEN,
                Items.WARPED_FUNGUS,
                Items.CRIMSON_FUNGUS
        );
    }

    public void tier2() {
        tag(Tags.Items.TIER_2_POTION_INGREDIENTS).add(
                Items.COBWEB,
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
                Items.RED_MUSHROOM_BLOCK
        );
    }

    public void tier3() {
        tag(Tags.Items.TIER_3_POTION_INGREDIENTS).add(
                Items.GOLDEN_APPLE,
                Items.AMETHYST_CLUSTER,
                Items.SHULKER_SHELL,
                Items.NAUTILUS_SHELL,
                Items.TOTEM_OF_UNDYING
        );
    }
}
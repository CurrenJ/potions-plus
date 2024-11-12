package grill24.potionsplus.data;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.ppId;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {
    /**
     * Constructs an advancement provider using the generators to write the
     * advancements to a file.
     *
     * @param output             the target directory of the data generator
     * @param registries         a future of a lookup for registries and their objects
     * @param existingFileHelper a helper used to find whether a file exists
     * @param subProviders       the generators used to create the advancements
     */
    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<AdvancementGenerator> subProviders) {
        super(output, registries, existingFileHelper, List.of(new PotionsPlusAdvancementGenerator()));
    }

    // Brewing Cauldron advancements
    public static final ResourceLocation CREATE_BREWING_CAULDRON = ppId("root");
    public static final ResourceLocation BREW_AWKWARD_POTION = ppId("brew_awkward_potion");
    public static final ResourceLocation BREW_ANY_POTION = ppId("brew_any_potion");

    // Abyssal Trove advancements
    public static final ResourceLocation CREATE_ABYSSAL_TROVE = ppId("create_abyssal_trove");
    public static final ResourceLocation ADD_FIRST_TO_ABYSSAL_TROVE = ppId("add_first_to_abyssal_trove");
    public static final ResourceLocation ADD_COMMON_TO_ABYSSAL_TROVE = ppId("add_common_to_abyssal_trove");
    public static final ResourceLocation ADD_RARE_TO_ABYSSAL_TROVE = ppId("add_rare_to_abyssal_trove");

    // Sanguine Altar advancements
    public static final ResourceLocation CREATE_SANGUINE_ALTAR = ppId("create_sanguine_altar");

    // Clothesline advancements
    public static final ResourceLocation CREATE_CLOTHESLINE = ppId("create_clothesline");
    public static final ResourceLocation DRY_ROTTEN_FLESH = ppId("dry_rotten_flesh");

    // Biome advancements
    public static final ResourceLocation ARID_CAVE = ppId("arid_cave");
    public static final ResourceLocation ICE_CAVE = ppId("ice_cave");
    public static final ResourceLocation VOLCANIC_CAVE = ppId("volcanic_cave");

    private static final class PotionsPlusAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder create_brewing_cauldron = createBrewingCauldronAdvancements(saver, existingFileHelper);
            createAbyssalTroveAdvancements(saver, existingFileHelper, create_brewing_cauldron);
            createSanguineAltarAdvancements(saver, existingFileHelper, create_brewing_cauldron);
            createClotheslineAdvancements(saver, existingFileHelper, create_brewing_cauldron);
            createOtherAdvancements(registries, saver, existingFileHelper, create_brewing_cauldron);
            // Biome advancements are generated manually
        }
    }

    private static void createOtherAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper, AdvancementHolder createBrewingCauldron) {
        Advancement.Builder acquire_ore_flower = Advancement.Builder.advancement()
                .parent(createBrewingCauldron)
                .display(
                        new ItemStack(Blocks.COPPER_CHRYSANTHEMUM.value()),
                        Component.translatable("advancements.potionsplus.acquire_ore_flower.title"),
                        Component.translatable("advancements.potionsplus.acquire_ore_flower.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100));

        List<String> oreFlowerIds = new ArrayList<>();
        for (OreFlowerBlock block : BuiltInRegistries.BLOCK.stream().filter(b -> b instanceof OreFlowerBlock).map(b -> (OreFlowerBlock) b).toList()) {
            String id = "acquire_ore_flower_" + BuiltInRegistries.BLOCK.getKey(block).getPath();
            acquire_ore_flower.addCriterion(id, InventoryChangeTrigger.TriggerInstance.hasItems(block));
            oreFlowerIds.add(id);
        }

        acquire_ore_flower.requirements(AdvancementRequirements.anyOf(oreFlowerIds))
                .save(saver, ppId("acquire_ore_flower"), existingFileHelper);


        AdvancementHolder sulfurShard = Advancement.Builder.advancement()
                .parent(createBrewingCauldron)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.SULFUR_SHARD),
                        Component.translatable("advancements.potionsplus.acquire_sulfur_shard.title"),
                        Component.translatable("advancements.potionsplus.acquire_sulfur_shard.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_sulfur_shard", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.SULFUR_SHARD.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_sulfur_shard")))
                .save(saver, ppId("acquire_sulfur_shard"), existingFileHelper);

        AdvancementHolder sulfuricAcid = Advancement.Builder.advancement()
                .parent(sulfurShard)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.SULFURIC_ACID),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.title"),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_sulfuric_acid", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE.getKey(), PpIngredient.of(new ItemStack(grill24.potionsplus.core.Items.SULFURIC_ACID))))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_sulfuric_acid")))
                .save(saver, ppId("acquire_sulfuric_acid"), existingFileHelper);

        AdvancementHolder acquireUraniumOre = Advancement.Builder.advancement()
                .parent(sulfurShard)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.RAW_URANIUM),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ore.title"),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ore.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_raw_uranium", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.RAW_URANIUM.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_raw_uranium")))
                .save(saver, ppId("acquire_raw_uranium"), existingFileHelper);

        AdvancementHolder acquireUraniumIngot = Advancement.Builder.advancement()
                .parent(acquireUraniumOre)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.URANIUM_INGOT),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ingot.title"),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ingot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_uranium_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.URANIUM_INGOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_uranium_ingot")))
                .save(saver, ppId("acquire_uranium_ingot"), existingFileHelper);
    }

    private static void createSanguineAltarAdvancements(Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_sanguine_altar = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(Blocks.SANGUINE_ALTAR.value()),
                        Component.translatable("advancements.potionsplus.sanguine_altar.title"),
                        Component.translatable("advancements.potionsplus.sanguine_altar.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("create_sanguine_altar", CreatePotionsPlusBlockTrigger.TriggerInstance.create(Blocks.SANGUINE_ALTAR.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_sanguine_altar")))
                .save(saver, CREATE_SANGUINE_ALTAR, existingFileHelper);

        AdvancementHolder convert_item_in_sanguine_altar = Advancement.Builder.advancement()
                .parent(create_sanguine_altar)
                .display(
                        new ItemStack(Items.AMETHYST_SHARD),
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.title"),
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("convert_item_in_sanguine_altar", CraftRecipeTrigger.TriggerInstance.create(Recipes.SANGUINE_ALTAR_RECIPE.getKey()))
                .requirements(AdvancementRequirements.allOf(List.of("convert_item_in_sanguine_altar")))
                .save(saver, ppId("convert_item_in_sanguine_altar"), existingFileHelper);
    }

    private static @NotNull AdvancementHolder createBrewingCauldronAdvancements(Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        AdvancementHolder create_brewing_cauldron = Advancement.Builder.advancement()
        .display(
                // The advancement icon. Can be an ItemStack or an ItemLike.
                new ItemStack(Blocks.BREWING_CAULDRON.value()),
                // The advancement title and description. Don't forget to add translations for these!
                Component.translatable("advancements.potionsplus.brewing_cauldron.title"),
                Component.translatable("advancements.potionsplus.brewing_cauldron.description"),
                // The background texture. Use null if you don't want a background texture (for non-root advancements).
                ppId("textures/block/cooblestone.png"),
                // The frame type. Valid values are AdvancementType.TASK, CHALLENGE, or GOAL.
                AdvancementType.TASK,
                // Whether to show the advancement toast or not.
                true,
                // Whether to announce the advancement into chat or not.
                true,
                // Whether the advancement should be hidden or not.
                false)
        .rewards(
                AdvancementRewards.Builder
                .experience(100)
                .addLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "chests/igloo"))))
        .addCriterion("create_brewing_cauldron", CreatePotionsPlusBlockTrigger.TriggerInstance.create(Blocks.BREWING_CAULDRON.value().defaultBlockState()))
        .requirements(AdvancementRequirements.allOf(List.of("create_brewing_cauldron")))
        .save(saver, CREATE_BREWING_CAULDRON, existingFileHelper);

        AdvancementHolder brew_awkward_potion = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(Items.NETHER_WART),
                        Component.translatable("advancements.potionsplus.brew_awkward_potion.title"),
                        Component.translatable("advancements.potionsplus.brew_awkward_potion.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("brew_awkward_potion", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE.getKey(), PpIngredient.of(PUtil.createPotionItemStack(Potions.AWKWARD, PUtil.PotionType.POTION)), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER)))
                .requirements(AdvancementRequirements.allOf(List.of("brew_awkward_potion")))
                .save(saver, BREW_AWKWARD_POTION, existingFileHelper);

        AdvancementHolder brew_any_potion = Advancement.Builder.advancement()
                .parent(brew_awkward_potion)
                .display(
                        PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION),
                        Component.translatable("advancements.potionsplus.brew_any_potion.title"),
                        Component.translatable("advancements.potionsplus.brew_any_potion.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("brew_any_potion", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE.getKey(), PpIngredient.of(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION)), List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)))
                .requirements(AdvancementRequirements.allOf(List.of("brew_any_potion")))
                .save(saver, BREW_ANY_POTION, existingFileHelper);

        AdvancementHolder acquire_moss = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.MOSS),
                        Component.translatable("advancements.potionsplus.acquire_moss.title"),
                        Component.translatable("advancements.potionsplus.acquire_moss.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_moss", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.MOSS.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_moss")))
                .save(saver, ppId("acquire_moss"), existingFileHelper);

        AdvancementHolder acquire_salt = Advancement.Builder.advancement()
                .parent(acquire_moss)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.SALT),
                        Component.translatable("advancements.potionsplus.acquire_salt.title"),
                        Component.translatable("advancements.potionsplus.acquire_salt.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_salt", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.SALT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_salt")))
                .save(saver, ppId("acquire_salt"), existingFileHelper);

        AdvancementHolder acquire_wormroot = Advancement.Builder.advancement()
                .parent(acquire_salt)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.WORMROOT),
                        Component.translatable("advancements.potionsplus.acquire_wormroot.title"),
                        Component.translatable("advancements.potionsplus.acquire_wormroot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_wormroot", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.WORMROOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_wormroot")))
                .save(saver, ppId("acquire_wormroot"), existingFileHelper);

        AdvancementHolder acquire_rotten_wormroot = Advancement.Builder.advancement()
                .parent(acquire_wormroot)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.ROTTEN_WORMROOT),
                        Component.translatable("advancements.potionsplus.acquire_rotten_wormroot.title"),
                        Component.translatable("advancements.potionsplus.acquire_rotten_wormroot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_rotten_wormroot", InventoryChangeTrigger.TriggerInstance.hasItems(grill24.potionsplus.core.Items.ROTTEN_WORMROOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_rotten_wormroot")))
                .save(saver, ppId("acquire_rotten_wormroot"), existingFileHelper);


        return create_brewing_cauldron;
    }

    private static void createClotheslineAdvancements(Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_clothesline = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(Blocks.CLOTHESLINE.value()),
                        Component.translatable("advancements.potionsplus.clothesline.title"),
                        Component.translatable("advancements.potionsplus.clothesline.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("create_clothesline", CreatePotionsPlusBlockTrigger.TriggerInstance.create(Blocks.CLOTHESLINE.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_clothesline")))
                .save(saver, CREATE_CLOTHESLINE, existingFileHelper);

        AdvancementHolder dry_rotten_flesh = Advancement.Builder.advancement()
                .parent(create_clothesline)
                .display(
                        new ItemStack(Items.ROTTEN_FLESH),
                        Component.translatable("advancements.potionsplus.dry_rotten_flesh.title"),
                        Component.translatable("advancements.potionsplus.dry_rotten_flesh.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("dry_rotten_flesh", CraftRecipeTrigger.TriggerInstance.create(Recipes.CLOTHESLINE_RECIPE.getKey(), PpIngredient.of(new ItemStack(Items.LEATHER))))
                .requirements(AdvancementRequirements.allOf(List.of("dry_rotten_flesh")))
                .save(saver, DRY_ROTTEN_FLESH, existingFileHelper);
    }

    private static void createAbyssalTroveAdvancements(Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_abyssal_trove = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(Blocks.ABYSSAL_TROVE.value()),
                        Component.translatable("advancements.potionsplus.abyssal_trove.title"),
                        Component.translatable("advancements.potionsplus.abyssal_trove.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("create_abyssal_trove", CreatePotionsPlusBlockTrigger.TriggerInstance.create(Blocks.ABYSSAL_TROVE.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_abyssal_trove")))
                .save(saver, CREATE_ABYSSAL_TROVE, existingFileHelper);

        AdvancementHolder add_first_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(create_abyssal_trove)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.GENERIC_ICON, 12),
                        Component.translatable("advancements.potionsplus.add_first_ingredient_to_abyssal_trove.title"),
                        Component.translatable("advancements.potionsplus.add_first_ingredient_to_abyssal_trove.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("add_first_ingredient_to_abyssal_trove", AbyssalTroveTrigger.TriggerInstance.create(0.001f))
                .requirements(AdvancementRequirements.allOf(List.of("add_first_ingredient_to_abyssal_trove")))
                .save(saver, ADD_FIRST_TO_ABYSSAL_TROVE, existingFileHelper);

        AdvancementHolder add_common_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(add_first_ingredient_to_abyssal_trove)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.GENERIC_ICON, 17),
                        Component.translatable("advancements.potionsplus.add_common_ingredient_to_abyssal_trove.title"),
                        Component.translatable("advancements.potionsplus.add_common_ingredient_to_abyssal_trove.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("add_common_ingredient_to_abyssal_trove", AbyssalTroveTrigger.TriggerInstance.create(0.001f, PotionUpgradeIngredients.Rarity.COMMON))
                .requirements(AdvancementRequirements.allOf(List.of("add_common_ingredient_to_abyssal_trove")))
                .save(saver, ADD_COMMON_TO_ABYSSAL_TROVE, existingFileHelper);

        AdvancementHolder add_rare_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(add_common_ingredient_to_abyssal_trove)
                .display(
                        new ItemStack(grill24.potionsplus.core.Items.GENERIC_ICON, 18),
                        Component.translatable("advancements.potionsplus.add_rare_ingredient_to_abyssal_trove.title"),
                        Component.translatable("advancements.potionsplus.add_rare_ingredient_to_abyssal_trove.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("add_rare_ingredient_to_abyssal_trove", AbyssalTroveTrigger.TriggerInstance.create(0.001f, PotionUpgradeIngredients.Rarity.RARE))
                .requirements(AdvancementRequirements.allOf(List.of("add_rare_ingredient_to_abyssal_trove")))
                .save(saver, ADD_RARE_TO_ABYSSAL_TROVE, existingFileHelper);
    }

    protected static Advancement.Builder addBiomes(Advancement.Builder builder, HolderLookup.Provider levelRegistry, List<ResourceKey<Biome>> biomes) {
        HolderGetter<Biome> holdergetter = levelRegistry.lookupOrThrow(Registries.BIOME);

        for (ResourceKey<Biome> resourcekey : biomes) {
            builder.addCriterion(
                    resourcekey.location().toString(),
                    PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(holdergetter.getOrThrow(resourcekey)))
            );
        }

        return builder;
    }
}

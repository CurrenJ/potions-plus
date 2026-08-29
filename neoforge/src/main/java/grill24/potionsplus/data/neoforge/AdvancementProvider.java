package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.neoforge.blocks.FlowerBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.alchemy.PotionContainer;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.ppId;

public class AdvancementProvider extends net.minecraft.data.advancements.AdvancementProvider {
    /**
     * Constructs an advancement provider using the generators to write the
     * advancements to a file.
     *
     * @param output     the target directory of the data generator
     * @param registries a future of a lookup for registries and their objects
     */
    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new PotionsPlusAdvancementGenerator()));
    }

    // Brewing Cauldron advancements
    public static final Identifier CREATE_BREWING_CAULDRON = ppId("root");
    public static final Identifier BREW_AWKWARD_POTION = ppId("brew_awkward_potion");
    public static final Identifier BREW_ANY_POTION = ppId("brew_any_potion");

    // Abyssal Trove advancements
    public static final Identifier CREATE_ABYSSAL_TROVE = ppId("create_abyssal_trove");
    public static final Identifier ADD_FIRST_TO_ABYSSAL_TROVE = ppId("add_first_to_abyssal_trove");
    public static final Identifier ADD_COMMON_TO_ABYSSAL_TROVE = ppId("add_common_to_abyssal_trove");
    public static final Identifier ADD_RARE_TO_ABYSSAL_TROVE = ppId("add_rare_to_abyssal_trove");

    // Sanguine Altar advancements
    public static final Identifier CREATE_SANGUINE_ALTAR = ppId("create_sanguine_altar");

    // Clothesline advancements
    public static final Identifier CREATE_CLOTHESLINE = ppId("create_clothesline");
    public static final Identifier DRY_ROTTEN_FLESH = ppId("dry_rotten_flesh");

    private static final class PotionsPlusAdvancementGenerator implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
            AdvancementHolder create_brewing_cauldron = createBrewingCauldronAdvancements(saver);
            createAbyssalTroveAdvancements(saver, create_brewing_cauldron);
            createSanguineAltarAdvancements(saver, create_brewing_cauldron);
            createClotheslineAdvancements(saver, create_brewing_cauldron);
            createOtherAdvancements(registries, saver, create_brewing_cauldron);
            // Biome advancements are generated manually
        }
    }

    private static void createOtherAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, AdvancementHolder createBrewingCauldron) {
        Advancement.Builder acquire_ore_flower = Advancement.Builder.advancement()
                .parent(createBrewingCauldron)
                .display(
                        FlowerBlocks.COPPER_CHRYSANTHEMUM.value(),
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
                .save(saver, ppId("acquire_ore_flower"));


        AdvancementHolder sulfurShard = Advancement.Builder.advancement()
                .parent(createBrewingCauldron)
                .display(
                        OreItems.SULFUR_SHARD.value(),
                        Component.translatable("advancements.potionsplus.acquire_sulfur_shard.title"),
                        Component.translatable("advancements.potionsplus.acquire_sulfur_shard.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_sulfur_shard", InventoryChangeTrigger.TriggerInstance.hasItems(OreItems.SULFUR_SHARD.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_sulfur_shard")))
                .save(saver, ppId("acquire_sulfur_shard"));

        AdvancementHolder sulfuricAcid = Advancement.Builder.advancement()
                .parent(sulfurShard)
                .display(
                        OreItems.SULFURIC_ACID.value(),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.title"),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_sulfuric_acid", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE_KEY, PpIngredient.of(new ItemStack(OreItems.SULFURIC_ACID))))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_sulfuric_acid")))
                .save(saver, ppId("acquire_sulfuric_acid"));
    }

    private static void createSanguineAltarAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_sanguine_altar = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        BlockEntityBlocks.SANGUINE_ALTAR.value(),
                        Component.translatable("advancements.potionsplus.sanguine_altar.title"),
                        Component.translatable("advancements.potionsplus.sanguine_altar.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("create_sanguine_altar", CreatePotionsPlusBlockTrigger.TriggerInstance.create(BlockEntityBlocks.SANGUINE_ALTAR.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_sanguine_altar")))
                .save(saver, CREATE_SANGUINE_ALTAR);

        AdvancementHolder convert_item_in_sanguine_altar = Advancement.Builder.advancement()
                .parent(create_sanguine_altar)
                .display(
                        Items.AMETHYST_SHARD,
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.title"),
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("convert_item_in_sanguine_altar", CraftRecipeTrigger.TriggerInstance.create(Recipes.SANGUINE_ALTAR_RECIPE_KEY))
                .requirements(AdvancementRequirements.allOf(List.of("convert_item_in_sanguine_altar")))
                .save(saver, ppId("convert_item_in_sanguine_altar"));
    }

    private static @NotNull AdvancementHolder createBrewingCauldronAdvancements(Consumer<AdvancementHolder> saver) {
        AdvancementHolder create_brewing_cauldron = Advancement.Builder.advancement()
                .display(
                        // The advancement icon.
                        BlockEntityBlocks.BREWING_CAULDRON.value(),
                        // The advancement title and description. Don't forget to add translations for these!
                        Component.translatable("advancements.potionsplus.brewing_cauldron.title"),
                        Component.translatable("advancements.potionsplus.brewing_cauldron.description"),
                        // The background texture. Use null if you don't want a background texture (for non-root advancements).
                        ppId("block/cooblestone"),
                        // The frame type. Valid values are AdvancementType.TASK, CHALLENGE, or GOAL.
                        AdvancementType.TASK,
                        // Whether to show the advancement toast or not.
                        true,
                        // Whether to announce the advancement into chat or not.
                        true,
                        // Whether the advancement should be hidden or not.
                        false)
                .rewards(
                        AdvancementRewards.Builder.experience(100)
                                .addLootTable(ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("minecraft", "chests/igloo"))))
                .addCriterion("create_brewing_cauldron", CreatePotionsPlusBlockTrigger.TriggerInstance.create(BlockEntityBlocks.BREWING_CAULDRON.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_brewing_cauldron")))
                .save(saver, CREATE_BREWING_CAULDRON);

        AdvancementHolder brew_awkward_potion = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        Items.NETHER_WART,
                        Component.translatable("advancements.potionsplus.brew_awkward_potion.title"),
                        Component.translatable("advancements.potionsplus.brew_awkward_potion.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("brew_awkward_potion", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE_KEY, PpIngredient.of(PotionContainer.POTION.create(Potions.AWKWARD)), List.of(EffectComparison.MatchCriteria.IGNORE_POTION_CONTAINER)))
                .requirements(AdvancementRequirements.allOf(List.of("brew_awkward_potion")))
                .save(saver, BREW_AWKWARD_POTION);

        AdvancementHolder brew_any_potion = Advancement.Builder.advancement()
                .parent(brew_awkward_potion)
                .display(
                        ItemStackTemplate.fromNonEmptyStack(PotionContainer.POTION.create(grill24.potionsplus.core.potion.Potions.ANY_POTION)),
                        Component.translatable("advancements.potionsplus.brew_any_potion.title"),
                        Component.translatable("advancements.potionsplus.brew_any_potion.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("brew_any_potion", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE_KEY, PpIngredient.of(PotionContainer.POTION.create(grill24.potionsplus.core.potion.Potions.ANY_POTION)), List.of(EffectComparison.MatchCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT)))
                .requirements(AdvancementRequirements.allOf(List.of("brew_any_potion")))
                .save(saver, BREW_ANY_POTION);

        AdvancementHolder acquire_moss = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        BrewingItems.MOSS.value(),
                        Component.translatable("advancements.potionsplus.acquire_moss.title"),
                        Component.translatable("advancements.potionsplus.acquire_moss.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_moss", InventoryChangeTrigger.TriggerInstance.hasItems(BrewingItems.MOSS.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_moss")))
                .save(saver, ppId("acquire_moss"));

        AdvancementHolder acquire_salt = Advancement.Builder.advancement()
                .parent(acquire_moss)
                .display(
                        BrewingItems.SALT.value(),
                        Component.translatable("advancements.potionsplus.acquire_salt.title"),
                        Component.translatable("advancements.potionsplus.acquire_salt.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_salt", InventoryChangeTrigger.TriggerInstance.hasItems(BrewingItems.SALT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_salt")))
                .save(saver, ppId("acquire_salt"));

        AdvancementHolder acquire_wormroot = Advancement.Builder.advancement()
                .parent(acquire_salt)
                .display(
                        BrewingItems.WORMROOT.value(),
                        Component.translatable("advancements.potionsplus.acquire_wormroot.title"),
                        Component.translatable("advancements.potionsplus.acquire_wormroot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_wormroot", InventoryChangeTrigger.TriggerInstance.hasItems(BrewingItems.WORMROOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_wormroot")))
                .save(saver, ppId("acquire_wormroot"));

        AdvancementHolder acquire_rotten_wormroot = Advancement.Builder.advancement()
                .parent(acquire_wormroot)
                .display(
                        BrewingItems.ROTTEN_WORMROOT.value(),
                        Component.translatable("advancements.potionsplus.acquire_rotten_wormroot.title"),
                        Component.translatable("advancements.potionsplus.acquire_rotten_wormroot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(50))
                .addCriterion("acquire_rotten_wormroot", InventoryChangeTrigger.TriggerInstance.hasItems(BrewingItems.ROTTEN_WORMROOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_rotten_wormroot")))
                .save(saver, ppId("acquire_rotten_wormroot"));


        return create_brewing_cauldron;
    }

    private static void createClotheslineAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_clothesline = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        BlockEntityBlocks.CLOTHESLINE.value(),
                        Component.translatable("advancements.potionsplus.clothesline.title"),
                        Component.translatable("advancements.potionsplus.clothesline.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("create_clothesline", CreatePotionsPlusBlockTrigger.TriggerInstance.create(BlockEntityBlocks.CLOTHESLINE.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_clothesline")))
                .save(saver, CREATE_CLOTHESLINE);

        AdvancementHolder dry_rotten_flesh = Advancement.Builder.advancement()
                .parent(create_clothesline)
                .display(
                        Items.ROTTEN_FLESH,
                        Component.translatable("advancements.potionsplus.dry_rotten_flesh.title"),
                        Component.translatable("advancements.potionsplus.dry_rotten_flesh.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("dry_rotten_flesh", CraftRecipeTrigger.TriggerInstance.create(Recipes.CLOTHESLINE_RECIPE_KEY, PpIngredient.of(new ItemStack(Items.LEATHER))))
                .requirements(AdvancementRequirements.allOf(List.of("dry_rotten_flesh")))
                .save(saver, DRY_ROTTEN_FLESH);
    }

    private static void createAbyssalTroveAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_abyssal_trove = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        BlockEntityBlocks.ABYSSAL_TROVE.value(),
                        Component.translatable("advancements.potionsplus.abyssal_trove.title"),
                        Component.translatable("advancements.potionsplus.abyssal_trove.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder
                        .experience(100))
                .addCriterion("create_abyssal_trove", CreatePotionsPlusBlockTrigger.TriggerInstance.create(BlockEntityBlocks.ABYSSAL_TROVE.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_abyssal_trove")))
                .save(saver, CREATE_ABYSSAL_TROVE);

        AdvancementHolder add_first_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(create_abyssal_trove)
                .display(
                        ItemStackTemplate.fromNonEmptyStack(DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.UNKNOWN_TEX_LOC)),
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
                .save(saver, ADD_FIRST_TO_ABYSSAL_TROVE);

        AdvancementHolder add_common_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(add_first_ingredient_to_abyssal_trove)
                .display(
                        ItemStackTemplate.fromNonEmptyStack(DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.COMMON_TEX_LOC)),
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
                .save(saver, ADD_COMMON_TO_ABYSSAL_TROVE);

        AdvancementHolder add_rare_ingredient_to_abyssal_trove = Advancement.Builder.advancement()
                .parent(add_common_ingredient_to_abyssal_trove)
                .display(
                        ItemStackTemplate.fromNonEmptyStack(DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.RARE_TEX_LOC)),
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
                .save(saver, ADD_RARE_TO_ABYSSAL_TROVE);
    }
}

package grill24.potionsplus.data;

import grill24.potionsplus.advancement.AbyssalTroveTrigger;
import grill24.potionsplus.advancement.AwardStatTrigger;
import grill24.potionsplus.advancement.CraftRecipeTrigger;
import grill24.potionsplus.advancement.CreatePotionsPlusBlockTrigger;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.enumerateResourceLocations;
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

    // Skill advancements
    public static final ResourceLocation SKILL_JOURNALS = ppId("skill_journals");
    public static final ResourceLocation[] MINE_COPPER_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_copper_ore_" + count));
    public static final ResourceLocation[] MINE_COAL_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_coal_ore_" + count));
    public static final ResourceLocation[] MINE_IRON_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_iron_ore_" + count));
    public static final ResourceLocation[] MINE_GOLD_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_gold_ore_" + count));
    public static final ResourceLocation[] MINE_DIAMOND_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_diamond_ore_" + count));
    public static final ResourceLocation[] MINE_EMERALD_ORES = enumerateResourceLocations(HatItems.BLOCK_HAT_MODELS.length, count -> ppId("mine_emerald_ore_" + count));

    private static final class PotionsPlusAdvancementGenerator implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
            AdvancementHolder create_brewing_cauldron = createBrewingCauldronAdvancements(saver);
            createAbyssalTroveAdvancements(saver, create_brewing_cauldron);
            createSanguineAltarAdvancements(saver, create_brewing_cauldron);
            createClotheslineAdvancements(saver, create_brewing_cauldron);
            createSkillsAdvancements(saver, create_brewing_cauldron);
            createOtherAdvancements(registries, saver, create_brewing_cauldron);
            // Biome advancements are generated manually
        }
    }

    private static void createSkillsAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder root) {
        AdvancementHolder skillJournals = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        new ItemStack(BlockEntityBlocks.SKILL_JOURNALS.value()),
                        Component.translatable(Translations.ADVANCEMENTS_POTIONSPLUS_SKILL_JOURNALS_TITLE),
                        Component.translatable(Translations.ADVANCEMENTS_POTIONSPLUS_SKILL_JOURNALS_DESCRIPTION),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("skill_journals", InventoryChangeTrigger.TriggerInstance.hasItems(BlockEntityBlocks.SKILL_JOURNALS.value()))
                .requirements(AdvancementRequirements.allOf(List.of("skill_journals")))
                .save(saver, SKILL_JOURNALS);

        // Ore Block Hat advancements
        List<Block> copperOreBlocks = List.of(net.minecraft.world.level.block.Blocks.COPPER_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE, OreBlocks.SANDY_COPPER_ORE.value(), OreBlocks.STONEY_COPPER_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_copper_ore",
                        HatInfo.hats(MINE_COPPER_ORES, HatItems.COPPER_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.COPPER_ORE_HATS),
                        copperOreBlocks,
                        skillJournals);

        List<Block> coalOreBlocks = List.of(net.minecraft.world.level.block.Blocks.COAL_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE, OreBlocks.SANDY_COAL_ORE.value(), OreBlocks.STONEY_COAL_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_coal_ore",
                        HatInfo.hats(MINE_COAL_ORES, HatItems.COAL_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.COAL_ORE_HATS),
                        coalOreBlocks,
                        skillJournals);

        List<Block> ironOreBlocks = List.of(net.minecraft.world.level.block.Blocks.IRON_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE, OreBlocks.SANDY_IRON_ORE.value(), OreBlocks.STONEY_IRON_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_iron_ore",
                        HatInfo.hats(MINE_IRON_ORES, HatItems.IRON_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.IRON_ORE_HATS),
                        ironOreBlocks,
                        skillJournals);

        List<Block> goldOreBlocks = List.of(net.minecraft.world.level.block.Blocks.GOLD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE, OreBlocks.SANDY_GOLD_ORE.value(), OreBlocks.STONEY_GOLD_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_gold_ore",
                        HatInfo.hats(MINE_GOLD_ORES, HatItems.GOLD_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.GOLD_ORE_HATS),
                        goldOreBlocks,
                        skillJournals);

        List<Block> diamondOreBlocks = List.of(net.minecraft.world.level.block.Blocks.DIAMOND_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE, OreBlocks.SANDY_DIAMOND_ORE.value(), OreBlocks.STONEY_DIAMOND_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_diamond_ore",
                        HatInfo.hats(MINE_DIAMOND_ORES, HatItems.DIAMOND_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.DIAMOND_ORE_HATS),
                        diamondOreBlocks,
                        skillJournals);

        List<Block> emeraldOreBlocks = List.of(net.minecraft.world.level.block.Blocks.EMERALD_ORE, net.minecraft.world.level.block.Blocks.DEEPSLATE_EMERALD_ORE, OreBlocks.SANDY_EMERALD_ORE.value(), OreBlocks.STONEY_EMERALD_ORE.value());
        createOreHatAdvancement
                (saver,
                        "mine_emerald_ore",
                        HatInfo.hats(MINE_EMERALD_ORES, HatItems.EMERALD_ORE_HATS, new int[]{64, 128, 256, 512}, LootTables.EMERALD_ORE_HATS),
                        emeraldOreBlocks,
                        skillJournals);
    }

    private record HatInfo(ResourceLocation advancementId, ItemStack display, int amountRequired,
                           ResourceKey<LootTable> rewards) {
        public HatInfo(ResourceLocation advancementId, ItemStack display, int amountRequired, ResourceKey<LootTable> rewards) {
            this.advancementId = advancementId;
            this.display = display;
            this.amountRequired = amountRequired;
            this.rewards = rewards;
        }

        public static List<HatInfo> hats(ResourceLocation[] advancementIds, Holder<Item>[] hatItems, int[] amountsRequired, ResourceKey<LootTable>[] rewards) {
            if (hatItems.length != amountsRequired.length || hatItems.length != rewards.length || hatItems.length != advancementIds.length) {
                throw new IllegalArgumentException("All arrays must be the same length");
            }

            List<HatInfo> hatInfos = new ArrayList<>();
            for (int i = 0; i < hatItems.length; i++) {
                hatInfos.add(new HatInfo(
                        advancementIds[i],
                        new ItemStack(hatItems[i].value()),
                        amountsRequired[i],
                        rewards[i])
                );
            }
            return hatInfos;
        }
    }

    private static void createOreHatAdvancement(Consumer<AdvancementHolder> saver, String name, List<HatInfo> hatInfos, List<Block> acceptedBlocks, AdvancementHolder parent) {
        AdvancementHolder currentParent = parent;
        for (HatInfo hatInfo : hatInfos) {
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.parent(currentParent);
            builder.display(
                    hatInfo.display(),
                    Component.translatable("advancements.potionsplus.ore_hat." + name + "_" + hatInfo.amountRequired() + ".title", hatInfo.display().getHoverName()),
                    Component.translatable("advancements.potionsplus.ore_hat." + name + "_" + hatInfo.amountRequired() + ".description", hatInfo.display().getHoverName()),
                    null,
                    AdvancementType.TASK,
                    true,
                    true,
                    false);
            builder.rewards(AdvancementRewards.Builder.loot(hatInfo.rewards()));

            List<String> acceptedBlockKeys = acceptedBlocks.stream().map(b -> name + "_" + BuiltInRegistries.BLOCK.getKey(b).getPath()).toList();
            for (int i = 0; i < acceptedBlocks.size(); i++) {
                String criterionName = acceptedBlockKeys.get(i);
                Block block = acceptedBlocks.get(i);

                builder.addCriterion(criterionName, AwardStatTrigger.TriggerInstance.create(Stats.BLOCK_MINED.get(block).getName(), hatInfo.amountRequired()));
            }
            builder.requirements(AdvancementRequirements.anyOf(acceptedBlockKeys));

            currentParent = builder.save(saver, hatInfo.advancementId());
        }
    }

    private static void createOtherAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, AdvancementHolder createBrewingCauldron) {
        Advancement.Builder acquire_ore_flower = Advancement.Builder.advancement()
                .parent(createBrewingCauldron)
                .display(
                        new ItemStack(FlowerBlocks.COPPER_CHRYSANTHEMUM.value()),
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
                        new ItemStack(OreItems.SULFUR_SHARD),
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
                        new ItemStack(OreItems.SULFURIC_ACID),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.title"),
                        Component.translatable("advancements.potionsplus.acquire_sulfuric_acid.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_sulfuric_acid", CraftRecipeTrigger.TriggerInstance.create(Recipes.BREWING_CAULDRON_RECIPE.getKey(), PpIngredient.of(new ItemStack(OreItems.SULFURIC_ACID))))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_sulfuric_acid")))
                .save(saver, ppId("acquire_sulfuric_acid"));

        AdvancementHolder acquireUraniumOre = Advancement.Builder.advancement()
                .parent(sulfurShard)
                .display(
                        new ItemStack(OreItems.RAW_URANIUM),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ore.title"),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ore.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_raw_uranium", InventoryChangeTrigger.TriggerInstance.hasItems(OreItems.RAW_URANIUM.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_raw_uranium")))
                .save(saver, ppId("acquire_raw_uranium"));

        AdvancementHolder acquireUraniumIngot = Advancement.Builder.advancement()
                .parent(acquireUraniumOre)
                .display(
                        new ItemStack(OreItems.URANIUM_INGOT),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ingot.title"),
                        Component.translatable("advancements.potionsplus.acquire_uranium_ingot.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("acquire_uranium_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(OreItems.URANIUM_INGOT.value()))
                .requirements(AdvancementRequirements.allOf(List.of("acquire_uranium_ingot")))
                .save(saver, ppId("acquire_uranium_ingot"));
    }

    private static void createSanguineAltarAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_sanguine_altar = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(BlockEntityBlocks.SANGUINE_ALTAR.value()),
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
                        new ItemStack(Items.AMETHYST_SHARD),
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.title"),
                        Component.translatable("advancements.potionsplus.convert_item_in_sanguine_altar.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .rewards(AdvancementRewards.Builder.experience(100))
                .addCriterion("convert_item_in_sanguine_altar", CraftRecipeTrigger.TriggerInstance.create(Recipes.SANGUINE_ALTAR_RECIPE.getKey()))
                .requirements(AdvancementRequirements.allOf(List.of("convert_item_in_sanguine_altar")))
                .save(saver, ppId("convert_item_in_sanguine_altar"));
    }

    private static @NotNull AdvancementHolder createBrewingCauldronAdvancements(Consumer<AdvancementHolder> saver) {
        AdvancementHolder create_brewing_cauldron = Advancement.Builder.advancement()
                .display(
                        // The advancement icon. Can be an ItemStack or an ItemLike.
                        new ItemStack(BlockEntityBlocks.BREWING_CAULDRON.value()),
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
                                .addLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "chests/igloo"))))
                .addCriterion("create_brewing_cauldron", CreatePotionsPlusBlockTrigger.TriggerInstance.create(BlockEntityBlocks.BREWING_CAULDRON.value().defaultBlockState()))
                .requirements(AdvancementRequirements.allOf(List.of("create_brewing_cauldron")))
                .save(saver, CREATE_BREWING_CAULDRON);

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
                .save(saver, BREW_AWKWARD_POTION);

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
                .save(saver, BREW_ANY_POTION);

        AdvancementHolder acquire_moss = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(BrewingItems.MOSS),
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
                        new ItemStack(BrewingItems.SALT),
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
                        new ItemStack(BrewingItems.WORMROOT),
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
                        new ItemStack(BrewingItems.ROTTEN_WORMROOT),
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
                        new ItemStack(BlockEntityBlocks.CLOTHESLINE.value()),
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
                .save(saver, DRY_ROTTEN_FLESH);
    }

    private static void createAbyssalTroveAdvancements(Consumer<AdvancementHolder> saver, AdvancementHolder create_brewing_cauldron) {
        AdvancementHolder create_abyssal_trove = Advancement.Builder.advancement()
                .parent(create_brewing_cauldron)
                .display(
                        new ItemStack(BlockEntityBlocks.ABYSSAL_TROVE.value()),
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
                        DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.UNKNOWN_TEX_LOC),
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
                        DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.COMMON_TEX_LOC),
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
                        DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.RARE_TEX_LOC),
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

package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

import static grill24.potionsplus.utility.Utility.ppId;

@JeiPlugin
public class JeiPotionsPlusPlugin implements IModPlugin {
    public static IJeiRuntime JEI_RUNTIME;
    public static final ResourceLocation PLUGIN_UID = ppId("main");

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        // Register descriptions for block entities
        registerDescription(registration, BlockEntityBlocks.PARTICLE_EMITTER.value()); // Particle Emitter description
        registerDescription(registration, BlockEntityBlocks.BREWING_CAULDRON.value()); // Brewing Cauldron description
        registerDescription(registration, BlockEntityBlocks.HERBALISTS_LECTERN.value()); // Herbalist's Lectern description
        registerDescription(registration, BlockEntityBlocks.ABYSSAL_TROVE.value()); // Abyssal Trove description
        registerDescription(registration, BlockEntityBlocks.SANGUINE_ALTAR.value()); // Sanguine Altar description
        registerDescription(registration, BlockEntityBlocks.CLOTHESLINE.value()); // Clothesline description

        // Register descriptions for miscellaneous items and blocks
        registerDescription(registration, DecorationBlocks.DECORATIVE_FIRE.value()); // Decorative Fire description
        registerDescription(registration, BrewingItems.MOSS.value()); // Moss description
        registerDescription(registration, BrewingItems.SALT.value()); // Salt
        registerDescription(registration, BrewingItems.WORMROOT.value()); // Wormroot
        registerDescription(registration, BrewingItems.ROTTEN_WORMROOT.value()); // Rotten Wormroot
        registerDescription(registration, BrewingItems.LUNAR_BERRIES.value()); // Lunar Berries
        registerDescription(registration, HatItems.WREATH.value()); // Wreath
        registerDescription(registration, DecorationBlocks.COOBLESTONE.value()); // Cooblestone
        registerDescription(registration, DecorationBlocks.LAVA_GEYSER.value()); // Lava Geyser
        registerAllPotionsInfo(registration, Potions.getAllPotionAmpDurMatrices()); // Potion descriptions

        // Register recipes for custom JEI recipe categories
        if (Minecraft.getInstance().level != null) {
            // Register all brewing cauldron recipes
            registration.addRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE,
                    Recipes.recipes.byType(Recipes.BREWING_CAULDRON_RECIPE.value())
                            .stream().map(RecipeHolder::value).filter(BrewingCauldronRecipe::canShowInJei).toList());

            // Register all clothesline recipes
            registration.addRecipes(ClotheslineRecipeCategory.CLOTHESLINE_RECIPE_TYPE,
                    Recipes.recipes.byType(Recipes.CLOTHESLINE_RECIPE.value())
                            .stream().map(RecipeHolder::value).toList());
        }
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BrewingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ClotheslineRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;

        tryUpdateJeiHiddenBrewingCauldronRecipes();
    }

    private static void tryUpdateJeiHiddenBrewingCauldronRecipes() {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;


        if (JEI_RUNTIME != null && level != null && level.isClientSide && player != null && Recipes.recipes != null) {
            List<BrewingCauldronRecipe> brewingCauldronRecipeList = Recipes.recipes.byType(Recipes.BREWING_CAULDRON_RECIPE.value()).stream().map(RecipeHolder::value).toList();
            JEI_RUNTIME.getRecipeManager().hideRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, brewingCauldronRecipeList);

            // Filter recipes to those that match known recipes and unhide them
            Stream<BrewingCauldronRecipe> stream = Recipes.recipes.byType(Recipes.BREWING_CAULDRON_RECIPE.value()).stream()
                    .filter(recipe ->
                            (SavedData.instance.getData(player).isRecipeKnown(recipe.id()) || !recipe.value().isSeededRuntimeRecipe() || Debug.shouldRevealAllRecipes)
                                    && recipe.value().canShowInJei()
                    ).map(RecipeHolder::value);
            JEI_RUNTIME.getRecipeManager().unhideRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, stream.toList());
        }
    }

    public static void scheduleUpdateJeiHiddenBrewingCauldronRecipes() {
        Minecraft.getInstance().execute(JeiPotionsPlusPlugin::tryUpdateJeiHiddenBrewingCauldronRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, BlockEntityBlocks.BREWING_CAULDRON.value());
        registration.addCraftingStation(ClotheslineRecipeCategory.CLOTHESLINE_RECIPE_TYPE, BlockEntityBlocks.CLOTHESLINE.value());
    }

    private static void registerAllPotionsInfo(IRecipeRegistration registration, PotionBuilder.PotionsPlusPotionGenerationData... potionData) {
        for (PotionBuilder.PotionsPlusPotionGenerationData data : potionData) {
            String effectName = data.getName();
            if (!effectName.isBlank()) {
                String descriptionKey = "jei.potionsplus." + effectName + ".description";
                MutableComponent descriptionComponent = Component.translatable(descriptionKey);
                registerPotionsInfo(registration, data, descriptionComponent);
            }
        }
    }

    private static void registerPotionsInfo(IRecipeRegistration registration, PotionBuilder.PotionsPlusPotionGenerationData potionData, Component... descriptionComponents) {
        Holder<Potion> potion = potionData.potion;
        if (potion.isBound()) {
            registerPotionInfo(registration, potion.value(), descriptionComponents);
        }
    }

    private static void registerPotionInfo(IRecipeRegistration registration, Potion potion, Component... descriptionComponents) {
        ItemStack potionItem = PotionContents.createItemStack(Items.POTION, PUtil.getPotionHolder(potion));
        ItemStack splashPotionItem = PotionContents.createItemStack(Items.SPLASH_POTION, PUtil.getPotionHolder(potion));
        ItemStack lingeringPotionItem = PotionContents.createItemStack(Items.LINGERING_POTION, PUtil.getPotionHolder(potion));

        registration.addItemStackInfo(potionItem, descriptionComponents);
        registration.addItemStackInfo(splashPotionItem, descriptionComponents);
        registration.addItemStackInfo(lingeringPotionItem, descriptionComponents);
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    private static void registerDescription(IRecipeRegistration registration, ItemLike item) {
        MutableComponent description = Component.translatable("jei.potionsplus." + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath() + ".description");
        registration.addItemStackInfo(new ItemStack(item), description);
    }
}

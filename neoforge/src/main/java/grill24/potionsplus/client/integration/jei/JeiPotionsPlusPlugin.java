package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
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
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Particle Emitter description
        registerDescription(registration, grill24.potionsplus.core.Blocks.PARTICLE_EMITTER.value());

        // Brewing Cauldron description
        registerDescription(registration, grill24.potionsplus.core.Blocks.BREWING_CAULDRON.value());

        // Herbalist's Lectern description
        registerDescription(registration, grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN.value());

        // Abyssal Trove description
        registerDescription(registration, grill24.potionsplus.core.Blocks.ABYSSAL_TROVE.value());

        // Sanguine Altar description
        registerDescription(registration, grill24.potionsplus.core.Blocks.SANGUINE_ALTAR.value());

        // Clothesline description
        registerDescription(registration, grill24.potionsplus.core.Blocks.CLOTHESLINE.value());

        // Decorative Fire description
        registerDescription(registration, grill24.potionsplus.core.Blocks.DECORATIVE_FIRE.value());

        // Moss description
        registerDescription(registration, grill24.potionsplus.core.Items.MOSS.value());

        // Salt
        registerDescription(registration, grill24.potionsplus.core.Items.SALT.value());

        // Wormroot
        registerDescription(registration, grill24.potionsplus.core.Items.WORMROOT.value());

        // Rotten Wormroot
        registerDescription(registration, grill24.potionsplus.core.Items.ROTTEN_WORMROOT.value());

        // Lunar Berries
        registerDescription(registration, grill24.potionsplus.core.Items.LUNAR_BERRIES.value());

        // Wreath
        registerDescription(registration, grill24.potionsplus.core.Items.WREATH.value());

        // Cooblestone
        registerDescription(registration, Blocks.COOBLESTONE.value());

        // Lava Geyser
        registerDescription(registration, grill24.potionsplus.core.Blocks.LAVA_GEYSER.value());

        // Potion descriptions
        registerAllPotionsInfo(registration, Potions.getAllPotionAmpDurMatrices());

        // Register all known recipes
        if (Minecraft.getInstance().level != null) {
            registration.addRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE,
                    Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.value())
                            .stream().map(RecipeHolder::value).toList());
        }
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BrewingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEI_RUNTIME = jeiRuntime;

        tryUpdateJeiHiddenBrewingCauldronRecipes();
    }

    private static void tryUpdateJeiHiddenBrewingCauldronRecipes() {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (JEI_RUNTIME != null && level != null && level.isClientSide && player != null) {
            List<BrewingCauldronRecipe> brewingCauldronRecipeList = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.value()).stream().map(RecipeHolder::value).toList();
            JEI_RUNTIME.getRecipeManager().hideRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, brewingCauldronRecipeList);

            // Filter recipes to those that match known recipes and unhide them
            Stream<BrewingCauldronRecipe> stream = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.value()).stream()
                    .filter(recipe ->
                            (SavedData.instance.getData(player).isRecipeKnown(recipe.id().toString()) || !recipe.value().isSeededRuntimeRecipe() || PotionsPlus.Debug.shouldRevealAllRecipes)
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
        registration.addRecipeCatalyst(new ItemStack(grill24.potionsplus.core.Blocks.BREWING_CAULDRON.value()), BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE);
        // TODO: Add clothesline recipes
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

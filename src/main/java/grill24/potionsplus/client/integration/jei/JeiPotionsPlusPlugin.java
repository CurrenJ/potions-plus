package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.core.Potions;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@JeiPlugin
public class JeiPotionsPlusPlugin implements IModPlugin {
    public static IJeiRuntime JEI_RUNTIME;
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ModInfo.MOD_ID, "main");

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Particle Emitter description
        MutableComponent particleEmitterDescription = new TranslatableComponent("jei.potionsplus.particle_emitter.description");
        registration.addIngredientInfo(new ItemStack(grill24.potionsplus.core.Items.PARTICLE_EMITTER.get()), VanillaTypes.ITEM, particleEmitterDescription);

        // Brewing Cauldron description
        MutableComponent brewingCauldronDescription = new TranslatableComponent("jei.potionsplus.brewing_cauldron.description");
        registration.addIngredientInfo(new ItemStack(grill24.potionsplus.core.Blocks.BREWING_CAULDRON.get()), VanillaTypes.ITEM, brewingCauldronDescription);

        // Herbalist's Lectern description
        MutableComponent herbalistsLecternDescription = new TranslatableComponent("jei.potionsplus.herbalists_lectern.description");
        registration.addIngredientInfo(new ItemStack(grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN.get()), VanillaTypes.ITEM, herbalistsLecternDescription);

        // Abyssal Trove description
        MutableComponent abyssalTroveDescription = new TranslatableComponent("jei.potionsplus.abyssal_trove.description");
        registration.addIngredientInfo(new ItemStack(grill24.potionsplus.core.Blocks.ABYSSAL_TROVE.get()), VanillaTypes.ITEM, abyssalTroveDescription);

        // Potion descriptions
        registerAllPotionsInfo(registration, Potions.getAllPotionAmpDurMatrices());

        // Register all known recipes
        if(Minecraft.getInstance().level != null) {
            registration.addRecipes(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()), BrewingCauldronRecipeCategory.BREWING_CAULDRON_CATEGORY);
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

    public static void tryUpdateJeiHiddenBrewingCauldronRecipes() {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if(JEI_RUNTIME != null && level != null && level.isClientSide && player != null) {
            List<BrewingCauldronRecipe> brewingCauldronRecipeList = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get());
            brewingCauldronRecipeList.forEach(recipe -> JEI_RUNTIME.getRecipeManager().hideRecipe(recipe, BrewingCauldronRecipeCategory.BREWING_CAULDRON_CATEGORY));

            // Filter recipes to those that match known recipes and unhide them
            Stream<BrewingCauldronRecipe> stream = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).stream()
                    .filter(recipe -> SavedData.instance.getData(player).knownRecipesContains(recipe.getId().toString()));

            stream.forEach(recipe -> {
                JEI_RUNTIME.getRecipeManager().unhideRecipe(recipe, BrewingCauldronRecipeCategory.BREWING_CAULDRON_CATEGORY);
            });
        }
    }

    public static void scheduleUpdateJeiHiddenBrewingCauldronRecipes() {
        Minecraft.getInstance().execute(JeiPotionsPlusPlugin::tryUpdateJeiHiddenBrewingCauldronRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(grill24.potionsplus.core.Blocks.BREWING_CAULDRON.get()), BrewingCauldronRecipeCategory.BREWING_CAULDRON_CATEGORY);
    }

    private static void registerAllPotionsInfo(IRecipeRegistration registration, Potions.PotionsAmpDurMatrix... potionsAmpDurMatrix) {
        for(Potions.PotionsAmpDurMatrix matrix : potionsAmpDurMatrix) {
            String effectName = matrix.getEffectName();
            if(!effectName.isBlank()) {
                String descriptionKey = "jei.potionsplus." + effectName + ".description";
                MutableComponent descriptionComponent = new TranslatableComponent(descriptionKey);
                registerPotionsInfo(registration, matrix, descriptionComponent);
            }
        }
    }

    private static void registerPotionsInfo(IRecipeRegistration registration, Potions.PotionsAmpDurMatrix potionsAmpDurMatrix, Component... descriptionComponents) {
        for(int a = 0; a < potionsAmpDurMatrix.potions.length; a++) {
            for(int d = 0; d < potionsAmpDurMatrix.potions[a].length; d++) {
                RegistryObject<Potion> potion = potionsAmpDurMatrix.potions[a][d];
                if(potion.isPresent()) {
                    registerPotionInfo(registration, potion.get(), descriptionComponents);
                }
            }
        }
    }

    private static void registerPotionInfo(IRecipeRegistration registration, Potion potion, Component... descriptionComponents) {
        ItemStack potionItem = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
        ItemStack splashPotionItem = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
        ItemStack lingeringPotionItem = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);

        registration.addIngredientInfo(potionItem, VanillaTypes.ITEM, descriptionComponents);
        registration.addIngredientInfo(splashPotionItem, VanillaTypes.ITEM, descriptionComponents);
        registration.addIngredientInfo(lingeringPotionItem, VanillaTypes.ITEM, descriptionComponents);
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }
}

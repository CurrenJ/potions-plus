package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.ClientCommands;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
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
        registerDescription(registration, grill24.potionsplus.core.Blocks.PARTICLE_EMITTER.get());

        // Brewing Cauldron description
        registerDescription(registration, grill24.potionsplus.core.Blocks.BREWING_CAULDRON.get());

        // Herbalist's Lectern description
        registerDescription(registration, grill24.potionsplus.core.Blocks.HERBALISTS_LECTERN.get());

        // Abyssal Trove description
        registerDescription(registration, grill24.potionsplus.core.Blocks.ABYSSAL_TROVE.get());

        // Sanguine Altar description
        registerDescription(registration, grill24.potionsplus.core.Blocks.SANGUINE_ALTAR.get());

        // Clothesline description
        registerDescription(registration, grill24.potionsplus.core.Blocks.CLOTHESLINE.get());

        // Decorative Fire description
        registerDescription(registration, grill24.potionsplus.core.Blocks.DECORATIVE_FIRE.get());

        // Moss description
        registerDescription(registration, grill24.potionsplus.core.Items.MOSS.get());

        // Salt
        registerDescription(registration, grill24.potionsplus.core.Items.SALT.get());

        // Wormroot
        registerDescription(registration, grill24.potionsplus.core.Items.WORMROOT.get());

        // Rotten Wormroot
        registerDescription(registration, grill24.potionsplus.core.Items.ROTTEN_WORMROOT.get());

        // Lunar Berries
        registerDescription(registration, grill24.potionsplus.core.Items.LUNAR_BERRIES.get());

        // Wreath
        registerDescription(registration, grill24.potionsplus.core.Items.WREATH.get());

        // Cooblestone
        registerDescription(registration, Blocks.COOBLESTONE.get());

        // Lava Geyser
        registerDescription(registration, grill24.potionsplus.core.Blocks.LAVA_GEYSER.get());

        // Potion descriptions
        registerAllPotionsInfo(registration, Potions.getAllPotionAmpDurMatrices());

        // Register all known recipes
        if (Minecraft.getInstance().level != null) {
            registration.addRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()));
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
            List<BrewingCauldronRecipe> brewingCauldronRecipeList = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get());
            JEI_RUNTIME.getRecipeManager().hideRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, brewingCauldronRecipeList);

            // Filter recipes to those that match known recipes and unhide them
            Stream<BrewingCauldronRecipe> stream = level.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()).stream()
                    .filter(recipe -> SavedData.instance.getData(player).knownRecipesContains(recipe.getId().toString()) || ClientCommands.shouldRevealAllRecipes);
            JEI_RUNTIME.getRecipeManager().unhideRecipes(BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE, stream.toList());
        }
    }

    public static void scheduleUpdateJeiHiddenBrewingCauldronRecipes() {
        Minecraft.getInstance().execute(JeiPotionsPlusPlugin::tryUpdateJeiHiddenBrewingCauldronRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(grill24.potionsplus.core.Blocks.BREWING_CAULDRON.get()), BrewingCauldronRecipeCategory.BREWING_CAULDRON_RECIPE_TYPE);
        // TODO: Add clothesline recipes
    }

    private static void registerAllPotionsInfo(IRecipeRegistration registration, PotionBuilder.PotionsAmpDurMatrix... potionsAmpDurMatrix) {
        for (PotionBuilder.PotionsAmpDurMatrix matrix : potionsAmpDurMatrix) {
            String effectName = matrix.getEffectName();
            if (!effectName.isBlank()) {
                String descriptionKey = "jei.potionsplus." + effectName + ".description";
                MutableComponent descriptionComponent = Component.translatable(descriptionKey);
                registerPotionsInfo(registration, matrix, descriptionComponent);
            }
        }
    }

    private static void registerPotionsInfo(IRecipeRegistration registration, PotionBuilder.PotionsAmpDurMatrix potionsAmpDurMatrix, Component... descriptionComponents) {
        for (int a = 0; a < potionsAmpDurMatrix.potions.length; a++) {
            for (int d = 0; d < potionsAmpDurMatrix.potions[a].length; d++) {
                RegistryObject<Potion> potion = potionsAmpDurMatrix.potions[a][d];
                if (potion.isPresent()) {
                    registerPotionInfo(registration, potion.get(), descriptionComponents);
                }
            }
        }
    }

    private static void registerPotionInfo(IRecipeRegistration registration, Potion potion, Component... descriptionComponents) {
        ItemStack potionItem = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
        ItemStack splashPotionItem = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
        ItemStack lingeringPotionItem = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);

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
        MutableComponent description = Component.translatable("jei.potionsplus." + ForgeRegistries.ITEMS.getKey(item.asItem()).getPath() + ".description");
        registration.addItemStackInfo(new ItemStack(item), description);
    }
}

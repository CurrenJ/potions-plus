package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.core.Potions;
import grill24.potionsplus.utility.ModInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JeiPotionsPlusPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ModInfo.MOD_ID, "main");

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Particle Emitter description
        MutableComponent particleEmitterDescription = new TranslatableComponent("jei.potionsplus.particle_emitter.description");
        registration.addIngredientInfo(new ItemStack(grill24.potionsplus.core.Items.PARTICLE_EMITTER.get()), VanillaTypes.ITEM, particleEmitterDescription);

        // Geode's Grace potion descriptions
        MutableComponent geodeGraceDescription = new TranslatableComponent("jei.potionsplus.geode_grace.description");
        registerPotionInfo(registration, Potions.GEODE_GRACE.get(), geodeGraceDescription);

        MutableComponent fallOfTheVoideDescription = new TranslatableComponent("jei.potionsplus.fall_of_the_void.description");
        registerPotionInfo(registration, Potions.FALL_OF_THE_VOID.get(), fallOfTheVoideDescription);

        // TODO Add recipe category for brewing cauldron

//        MutableComponent explodingDescription = new TranslatableComponent("jei.potionsplus.exploding.description");
//        registerPotionInfo(registration, Potions.EXPLODING.get(), explodingDescription);

//        MutableComponent magneticDescription = new TranslatableComponent("jei.potionsplus.magnetic.description");
//        registerPotionInfo(registration, Potions.MAGNETIC.get(), magneticDescription);

//        MutableComponent teleportationDescription = new TranslatableComponent("jei.potionsplus.teleportation.description");
//        registerPotionInfo(registration, Potions.TELEPORTATION.get(), teleportationDescription);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        // TODO Support seeded brewing cauldron recipes and show/hide based on BrewingKnowledge
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

package grill24.potionsplus.core;

import grill24.potionsplus.utility.BetterBrewingRecipe;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Potions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ModInfo.MOD_ID);

    public static final RegistryObject<Potion> GEODE_GRACE = POTIONS.register("geode_grace", () ->
            new Potion(new MobEffectInstance(MobEffects.GEODE_GRACE.get(), 6000), new MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 600)));

    public static final RegistryObject<Potion> FALL_OF_THE_VOID = POTIONS.register("fall_of_the_void", () ->
            new Potion(new MobEffectInstance(MobEffects.FALL_OF_THE_VOID.get(), 7200)));

    public static final RegistryObject<Potion> HASTE = POTIONS.register("haste", () ->
            new Potion(new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 12000)));

    public static final RegistryObject<Potion> LEVITATION = POTIONS.register("levitation", () ->
            new Potion(new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, 600)));

    public static final RegistryObject<Potion> LONG_LEVITATION = POTIONS.register("long_levitation", () ->
            new Potion(new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, 1200)));

    public static final RegistryObject<Potion> EXPLODING = POTIONS.register("exploding", () ->
            new Potion(new MobEffectInstance(MobEffects.EXPLODING.get(), 80)));

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.AMETHYST_CLUSTER, Potions.GEODE_GRACE.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.CHORUS_FRUIT, Potions.FALL_OF_THE_VOID.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.HASTE.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potions.LEVITATION.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_LEVITATION.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.TNT, Potions.EXPLODING.get()));
        });
    }
}

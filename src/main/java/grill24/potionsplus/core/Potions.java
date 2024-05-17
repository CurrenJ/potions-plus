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

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Potions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ModInfo.MOD_ID);

    public static final RegistryObject<Potion> GEODE_GRACE = POTIONS.register("geode_grace", () ->
            new Potion(new MobEffectInstance(MobEffects.GEODE_GRACE.get(), 6000), new MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 600)));

    public static final RegistryObject<Potion> FALL_OF_THE_VOID = POTIONS.register("fall_of_the_void", () ->
            new Potion(new MobEffectInstance(MobEffects.FALL_OF_THE_VOID.get(), 7200)));

    public static final RegistryObject<Potion> HASTE = POTIONS.register("haste", () ->
            new Potion(new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, 12000)));

    public static final PotionsAmpDurMatrix LEVITATION_POTIONS = new PotionsAmpDurMatrix("levitation",
            6, 4,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, 20 * (levels[1] + 1), levels[0])});

    public static final PotionsAmpDurMatrix MAGNETIC_POTIONS = new PotionsAmpDurMatrix("magnetic",
            3, 4,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.MAGNETIC.get(), 60 * (levels[1] + 1), levels[0])});

    public static final PotionsAmpDurMatrix EXPLODING_POTIONS = new PotionsAmpDurMatrix("exploding",
            3, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.EXPLODING.get(), 60, levels[0])});

    public static final PotionsAmpDurMatrix TELEPORTATION_POTIONS = new PotionsAmpDurMatrix("teleportation",
            4, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.TELEPORTATION.get(), 60, levels[0])});

    public static class PotionsAmpDurMatrix {
        public final RegistryObject<Potion>[][] potions;
        public PotionsAmpDurMatrix(RegistryObject<Potion>[][] potions) {
            this.potions = potions;
        }

        public PotionsAmpDurMatrix(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction) {
            this.potions = registerNewPotion(name, amplificationLevels, durationLevels, effectFunction);
        }

        public Potion get(int a, int d) {
            return potions[a][d].get();
        }

        public int getAmplificationLevels() {
            return potions.length;
        }

        public int getDurationLevels() {
            return potions[0].length;
        }
    }

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.AMETHYST_CLUSTER, Potions.GEODE_GRACE.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.ENDER_PEARL, Potions.FALL_OF_THE_VOID.get()));
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.HASTE.get()));
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potions.LEVITATION.get()));
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_LEVITATION.get()));
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.TNT, Potions.EXPLODING.get()));
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.HONEY_BLOCK, Potions.MAGNETIC.get()));
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.CHORUS_FRUIT, Potions.TELEPORTATION.get()));
        });
    }

    public static RegistryObject<Potion>[][] registerNewPotion(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction) {
        if(amplificationLevels < 1 || durationLevels < 1)
            throw new IllegalArgumentException("Amplification and duration levels must be at least 1");

        RegistryObject<Potion>[][] potions = new RegistryObject[amplificationLevels][durationLevels];
        for (int i = 0; i < amplificationLevels; i++) {

            for (int j = 0; j < durationLevels; j++) {
                int finalI = i;
                int finalJ = j;

                potions[i][j] = POTIONS.register(name + "_a" + i + "_d" + j, () ->
                        new Potion(name, effectFunction.apply(new int[]{finalI, finalJ}))
                );
            }
        }
        return potions;
    }
}

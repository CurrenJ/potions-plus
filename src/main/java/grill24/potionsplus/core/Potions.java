package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Potions {
    private static final Function<Integer, Integer> DEFAULT_DURATION_FUNCTION = (Integer durationLevel) -> (durationLevel+1) * 3600;
    private static final Function<Integer, Integer> LONGER_DURATION_FUNCTION = (Integer durationLevel) -> (durationLevel+1) * 5000;

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ModInfo.MOD_ID);
    public static final List<PotionsAmpDurMatrix> ALL_POTION_AMPLIFICATION_DURATION_MATRICES = new ArrayList<>();

    public static final PotionsAmpDurMatrix GEODE_GRACE_POTIONS = new PotionsAmpDurMatrix("geode_grace",
            1, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.GEODE_GRACE.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix FALL_OF_THE_VOID_POTIONS = new PotionsAmpDurMatrix("fall_of_the_void",
            1, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.FALL_OF_THE_VOID.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix HASTE_POTIONS = new PotionsAmpDurMatrix("haste",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED, DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix LEVITATION_POTIONS = new PotionsAmpDurMatrix("levitation",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, 200 * (levels[1] + 1), levels[0])});

    public static final PotionsAmpDurMatrix MAGNETIC_POTIONS = new PotionsAmpDurMatrix("magnetic",
            3, 4,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.MAGNETIC.get(), LONGER_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix EXPLODING_POTIONS = new PotionsAmpDurMatrix("exploding",
            3, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.EXPLODING.get(), 60, levels[0])});

    public static final PotionsAmpDurMatrix TELEPORTATION_POTIONS = new PotionsAmpDurMatrix("teleportation",
            4, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.TELEPORTATION.get(), 60, levels[0])});

    public static final PotionsAmpDurMatrix LOOTING_POTIONS = new PotionsAmpDurMatrix("looting",
            4, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.LOOTING.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix FORTUITOUS_FATE_POTIONS = new PotionsAmpDurMatrix("fortuitous_fate",
            4, 1,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.FORTUITOUS_FATE.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix METAL_DETECTING_POTIONS = new PotionsAmpDurMatrix("metal_detecting",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.METAL_DETECTING.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix GIANT_STEPS_POTIONS = new PotionsAmpDurMatrix("giant_steps",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.GIANT_STEPS.get(), DEFAULT_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix REACH_FOR_THE_STARS_POTIONS = new PotionsAmpDurMatrix("reach_for_the_stars",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.REACH_FOR_THE_STARS.get(), LONGER_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix NAUTICAL_NITRO_POTIONS = new PotionsAmpDurMatrix("nautical_nitro",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.NAUTICAL_NITRO.get(), LONGER_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix CROP_COLLECTOR_POTIONS = new PotionsAmpDurMatrix("crop_collector",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.CROP_COLLECTOR.get(), LONGER_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static final PotionsAmpDurMatrix BOTANICAL_BOOST_POTIONS = new PotionsAmpDurMatrix("botanical_boost",
            3, 2,
            (int[] levels) -> new MobEffectInstance[]{new MobEffectInstance(MobEffects.BOTANICAL_BOOST.get(), LONGER_DURATION_FUNCTION.apply(levels[1]), levels[0])});

    public static class PotionsAmpDurMatrix {
        public final RegistryObject<Potion>[][] potions;

        public PotionsAmpDurMatrix(RegistryObject<Potion>[][] potions) {
            this.potions = potions;
        }

        public PotionsAmpDurMatrix(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction) {
            this.potions = registerNewPotion(name, amplificationLevels, durationLevels, effectFunction);
            ALL_POTION_AMPLIFICATION_DURATION_MATRICES.add(this);
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

        public String getEffectName() {
            try {
                return potions[0][0].get().getEffects().get(0).getEffect().getRegistryName().getPath();
            } catch (NullPointerException e) {
                return "";
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.AMETHYST_CLUSTER, Potions.GEODE_GRACE.get()));
        });
    }

    public static RegistryObject<Potion>[][] registerNewPotion(String name, int amplificationLevels, int durationLevels, Function<int[], MobEffectInstance[]> effectFunction) {
        if (amplificationLevels < 1 || durationLevels < 1)
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

    public static PotionsAmpDurMatrix[] getAllPotionAmpDurMatrices() {
        return ALL_POTION_AMPLIFICATION_DURATION_MATRICES.toArray(new PotionsAmpDurMatrix[0]);
    }
}

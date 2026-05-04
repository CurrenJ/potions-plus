package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);

    public static final Holder<Potion> ANY_POTION;
    public static final Holder<Potion> ANY_OTHER_POTION;

    static {
        // Inject factory into common/ PotionBuilder before any PotionsPlusPotionGenerationData is constructed
        PotionBuilder.potionFactory = (name, effectSupplier) ->
                POTIONS.register(name, () -> new Potion(name, effectSupplier.get()));

        ANY_POTION = POTIONS.register("any_potion", () -> new Potion("Any Potion",
                new net.minecraft.world.effect.MobEffectInstance(MobEffects.ANY_POTION, 1200, 0)));
        ANY_OTHER_POTION = POTIONS.register("any_other_potion", () -> new Potion("Any Other Potion",
                new net.minecraft.world.effect.MobEffectInstance(MobEffects.ANY_OTHER_POTION, 1200, 0)));

        Potions.ANY_POTION = ANY_POTION;
        Potions.ANY_OTHER_POTION = ANY_OTHER_POTION;

        new PotionBuilder().name("geode_grace").effect(MobEffects.GEODE_GRACE).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("fall_of_the_void").effect(MobEffects.FALL_OF_THE_VOID).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("haste").effect(net.minecraft.world.effect.MobEffects.HASTE).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("levitation").effect(net.minecraft.world.effect.MobEffects.LEVITATION).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("magnetic").effect(MobEffects.MAGNETIC).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("exploding").effect(MobEffects.EXPLODING, 60).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("teleportation").effect(MobEffects.TELEPORTATION, 60).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("looting").effect(MobEffects.LOOTING).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("fortuitous_fate").effect(MobEffects.FORTUITOUS_FATE).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("metal_detecting").effect(MobEffects.METAL_DETECTING).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("giant_steps").effect(MobEffects.GIANT_STEPS).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("reach_for_the_stars").effect(MobEffects.REACH_FOR_THE_STARS).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("nautical_nitro").withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).effect(MobEffects.NAUTICAL_NITRO).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("crop_collector").effect(MobEffects.CROP_COLLECTOR).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("botanical_boost").effect(MobEffects.BOTANICAL_BOOST).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("saturation").effect(net.minecraft.world.effect.MobEffects.SATURATION).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("blindness").effect(net.minecraft.world.effect.MobEffects.BLINDNESS).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("nausea").effect(net.minecraft.world.effect.MobEffects.NAUSEA).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("hunger").effect(net.minecraft.world.effect.MobEffects.HUNGER).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("wither").effect(net.minecraft.world.effect.MobEffects.WITHER).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("slip_n_slide").effect(MobEffects.SLIP_N_SLIDE).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("harrowing_hands").effect(MobEffects.HARROWING_HANDS).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("shepherds_serenade").effect(MobEffects.SHEPHERDS_SERENADE).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("strength").effect(net.minecraft.world.effect.MobEffects.STRENGTH).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("resistance").effect(net.minecraft.world.effect.MobEffects.RESISTANCE).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("fire_resistance").effect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("water_breathing").effect(net.minecraft.world.effect.MobEffects.WATER_BREATHING).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("invisibility").effect(net.minecraft.world.effect.MobEffects.INVISIBILITY).withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("soul_mate").effect(MobEffects.SOUL_MATE).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("flying_time").effect(MobEffects.FLYING_TIME).withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1).build(Potions.ALL_POTION_GENERATION_DATA::add);
        new PotionBuilder().name("bouncing").effect(MobEffects.BOUNCING).build(Potions.ALL_POTION_GENERATION_DATA::add);
    }

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {});
    }
}

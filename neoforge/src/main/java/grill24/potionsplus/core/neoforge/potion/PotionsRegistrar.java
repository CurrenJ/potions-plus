package grill24.potionsplus.core.neoforge.potion;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Owns the NeoForge potion {@link DeferredRegister} and the 8 potions whose effects are still
 * {@code @EventBusSubscriber}-coupled NeoForge classes (Phase 7 bucket). The 23 portable potions
 * live in common {@code core.potion.Potions} and register at class-load through
 * {@link PotionBuilder#potionFactory} - wired to {@link #POTIONS} by {@code core.neoforge.PotionsPlus}'s
 * static block - appending to the common {@code Potions.ALL_POTION_GENERATION_DATA}. This registrar's
 * static fields run after {@code MobEffectsRegistrar} (PotionsPlus references it first) so the effect
 * holders they reference are populated. See docs/multi-loader-expansion.md Phase 4.
 */
public class PotionsRegistrar {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);

    public static final PotionBuilder.PotionsPlusPotionGenerationData GEODE_GRACE_POTIONS = new PotionBuilder()
            .name("geode_grace")
            .effect(MobEffects.GEODE_GRACE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData FALL_OF_THE_VOID_POTIONS = new PotionBuilder()
            .name("fall_of_the_void")
            .effect(MobEffects.FALL_OF_THE_VOID)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData EXPLODING_POTIONS = new PotionBuilder()
            .name("exploding")
            .effect(MobEffects.EXPLODING, 60)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData TELEPORTATION_POTIONS = new PotionBuilder()
            .name("teleportation")
            .effect(MobEffects.TELEPORTATION, 60)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData METAL_DETECTING_POTIONS = new PotionBuilder()
            .name("metal_detecting")
            .effect(MobEffects.METAL_DETECTING)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData SOUL_MATE_POTIONS = new PotionBuilder()
            .name("soul_mate")
            .effect(MobEffects.SOUL_MATE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData FLYING_TIME_POTIONS = new PotionBuilder()
            .name("flying_time")
            .effect(MobEffects.FLYING_TIME)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData BOUNCING_POTIONS = new PotionBuilder()
            .name("bouncing")
            .effect(MobEffects.BOUNCING)
            .build(grill24.potionsplus.core.potion.Potions.ALL_POTION_GENERATION_DATA::add);
}

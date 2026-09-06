package grill24.potionsplus.gametest;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.HarrowingHandsEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.GameType;

/**
 * In-world coverage for what each custom {@link net.minecraft.world.effect.MobEffect} the mod
 * registers actually does - {@code AlchemyGameTests} covers potion data and only borrows
 * {@code MAGNETIC}/{@code GEODE_GRACE} as arbitrary example holders, never their behaviour.
 *
 * <p>Ticking effects are exercised by calling their public {@code applyEffectTick} directly rather
 * than waiting on real duration/tick-interval scheduling - this is the game-test equivalent of the
 * "prefer synchronous" guidance in {@code BrewingCauldronGameTests}: it removes flakiness from tick
 * budgets and RNG-gated tick intervals, and the tick-interval math itself is scoped to unit tests.
 * Static one-shot hooks ({@code onPotionAdded}, {@code onEntityDeath}, etc.) are normally invoked by
 * loader-specific event listeners/mixins in {@code neoforge}/{@code fabric}/{@code forge}, which this
 * common testmod can't reach - so those are called directly too, testing the shared logic rather than
 * the per-loader wiring.
 *
 * <p>{@code ANY_POTION}/{@code ANY_OTHER_POTION} are markers covered by
 * {@code effectRegistryExcludesMarkerEffectsFromThePassivePool}. {@code SHEPHERDS_SERENADE} has no
 * server-observable behaviour - its only override calls {@code Minecraft.getInstance()} - so it is not
 * covered here. {@code NAUTICAL_NITRO}, {@code LOOTING} and {@code FORTUITOUS_FATE} expose pure
 * amplifier-to-value functions with no server/registry dependency and are covered by JUnit tests in
 * {@code common/src/test} instead.
 */
public final class EffectGameTests {

    private EffectGameTests() {}

    private static final BlockPos ORIGIN = new BlockPos(1, 1, 1);

    // ==================== ticking effects ====================

    /** Pulls a nearby dropped item towards the entity holding the effect. */
    public static void magneticPullsItemsTowardTheHolder(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);

        // spawnItem(Item, BlockPos) already converts a structure-relative pos to absolute internally -
        // passing an already-absolute pos here double-applies the offset and spawns the item far away.
        ItemEntity item = helper.spawnItem(Items.DIAMOND, ORIGIN.offset(3, 0, 0));
        double distanceBefore = item.position().distanceTo(holder.position());

        MobEffects.MAGNETIC.value().applyEffectTick(level, holder, 0);
        item.tick();

        double distanceAfter = item.position().distanceTo(holder.position());
        assertTrue(helper, distanceAfter < distanceBefore,
                "item did not move closer to the magnetic holder: " + distanceBefore + " -> " + distanceAfter);
        helper.succeed();
    }

    /** Destroys a fully-grown crop within range, harvesting it. */
    public static void cropCollectorHarvestsAMatureCropInRange(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockState matureWheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                helper.setBlock(ORIGIN.offset(x, 0, z), matureWheat);
            }
        }

        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        MobEffects.CROP_COLLECTOR.value().applyEffectTick(level, holder, 0);

        boolean anyDestroyed = false;
        for (int x = -1; x <= 1 && !anyDestroyed; x++) {
            for (int z = -1; z <= 1 && !anyDestroyed; z++) {
                anyDestroyed = !helper.getBlockState(ORIGIN.offset(x, 0, z)).is(Blocks.WHEAT);
            }
        }
        assertTrue(helper, anyDestroyed, "no mature crop in range was harvested");
        helper.succeed();
    }

    /** Forces growth on a young crop within range by driving its random tick. */
    public static void botanicalBoostAgesAYoungCropInRange(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockState youngWheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 0);
        BlockState wetFarmland = Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // CropBlock.randomTick requires a raw brightness of 9+ or it silently no-ops regardless
                // of how many random ticks are driven; farmland maximises growth speed once it can tick.
                helper.setBlock(ORIGIN.offset(x, -1, z), wetFarmland);
                helper.setBlock(ORIGIN.offset(x, 0, z), youngWheat);
                helper.setBlock(ORIGIN.offset(x, 2, z), Blocks.GLOWSTONE);
            }
        }

        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        // randomTick's own growth chance is probabilistic, so drive it enough times that a false
        // negative is statistically negligible rather than asserting after a single call.
        boolean anyGrew = false;
        for (int i = 0; i < 200 && !anyGrew; i++) {
            MobEffects.BOTANICAL_BOOST.value().applyEffectTick(level, holder, 0);
            for (int x = -1; x <= 1 && !anyGrew; x++) {
                for (int z = -1; z <= 1 && !anyGrew; z++) {
                    BlockState current = helper.getBlockState(ORIGIN.offset(x, 0, z));
                    anyGrew = current.is(Blocks.WHEAT) && current.getValue(CropBlock.AGE) > 0;
                }
            }
        }
        assertTrue(helper, anyGrew, "no young crop in range grew after 200 boosted ticks");
        helper.succeed();
    }

    /** Raises the holder's step height so they can walk up a full block without jumping. */
    public static void giantStepsRaisesStepHeight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        double baseStepHeight = holder.getAttributeValue(Attributes.STEP_HEIGHT);

        MobEffects.GIANT_STEPS.value().applyEffectTick(level, holder, 1);

        double boostedStepHeight = holder.getAttributeValue(Attributes.STEP_HEIGHT);
        assertTrue(helper, boostedStepHeight > baseStepHeight,
                "step height was not raised: " + baseStepHeight + " -> " + boostedStepHeight);
        helper.succeed();
    }

    /** Adding the effect increases interaction range attributes by amplifier + 1. */
    public static void reachForTheStarsIncreasesInteractionRange(GameTestHelper helper) {
        // BLOCK_INTERACTION_RANGE/ENTITY_INTERACTION_RANGE are player-only attributes - a Pig has no
        // AttributeInstance for either, so getAttributeValue would throw.
        var holder = helper.makeMockPlayer(GameType.SURVIVAL);
        holder.setPos(helper.absolutePos(ORIGIN).getCenter());
        double baseBlockRange = holder.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        double baseEntityRange = holder.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);

        holder.addEffect(new MobEffectInstance(MobEffects.REACH_FOR_THE_STARS, 200, 1));

        assertTrue(helper, holder.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) > baseBlockRange,
                "block interaction range was not increased");
        assertTrue(helper, holder.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) > baseEntityRange,
                "entity interaction range was not increased");
        helper.succeed();
    }

    /** Teleports the holder somewhere else in the world (chorus-fruit style). */
    public static void teleportationMovesTheHolder(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        var startPos = holder.position();

        // randomTeleport can fail to find a legal target on a given attempt (e.g. no solid ground at
        // the chosen height); retry rather than asserting after a single call.
        boolean moved = false;
        for (int i = 0; i < 20 && !moved; i++) {
            MobEffects.TELEPORTATION.value().applyEffectTick(level, holder, 0);
            moved = !holder.position().equals(startPos);
        }
        assertTrue(helper, moved, "the holder never teleported after 20 attempts");
        helper.succeed();
    }

    /** Grants nearby skeletons the bone-buddy effect. */
    public static void harrowingHandsGrantsBoneBuddyToNearbySkeletons(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        Skeleton skeleton = helper.spawnWithNoFreeWill(EntityType.SKELETON, ORIGIN.offset(3, 0, 0));

        // applyEffectTick reads the holder's own active HARROWING_HANDS instance for its duration, so
        // the effect has to actually be present on the holder, not just ticked.
        holder.addEffect(new MobEffectInstance(MobEffects.HARROWING_HANDS, 200, 0));
        MobEffects.HARROWING_HANDS.value().applyEffectTick(level, holder, 0);

        assertTrue(helper, skeleton.hasEffect(MobEffects.BONE_BUDDY),
                "the nearby skeleton did not gain bone_buddy");
        helper.succeed();
    }

    // ==================== one-shot static hooks ====================

    /** Expiry damages the holder and disturbs the world around them (an explosion). */
    public static void explodingDamagesTheHolderOnExpiry(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absolutePos(ORIGIN).getCenter());
        player.setHealth(20F);

        MobEffectInstance instance = new MobEffectInstance(MobEffects.EXPLODING, 1, 0);
        ExplodingEffect.onPotionExpiry(player, instance);

        assertTrue(helper, player.getHealth() < 20F,
                "the holder took no damage from the exploding effect expiring");
        helper.succeed();
    }

    /** Swaps a skeleton's target from players to other monsters while bone-buddy is active, and back. */
    public static void boneBuddyRetargetsASkeletonsAggro(GameTestHelper helper) {
        Skeleton skeleton = helper.spawnWithNoFreeWill(EntityType.SKELETON, ORIGIN);

        MobEffectInstance added = new MobEffectInstance(MobEffects.BONE_BUDDY, 200, 0, false, false, true);
        skeleton.addEffect(added);
        BoneBuddyEffect.onPotionAdded(skeleton, added);
        assertTrue(helper, hasMonsterTargetGoal(skeleton),
                "bone_buddy did not add a monster-targeting goal");

        skeleton.removeEffect(MobEffects.BONE_BUDDY);
        BoneBuddyEffect.onPotionExpired(skeleton, added);
        assertTrue(helper, !hasMonsterTargetGoal(skeleton),
                "the monster-targeting goal was not removed once bone_buddy expired");
        helper.succeed();
    }

    /**
     * {@code Mob.targetSelector} is protected; reflect into it the same way
     * {@link BoneBuddyEffect#getTargetSelector} does, since that helper itself is private.
     */
    private static boolean hasMonsterTargetGoal(Skeleton skeleton) {
        try {
            var field = net.minecraft.world.entity.Mob.class.getDeclaredField("targetSelector");
            field.setAccessible(true);
            var targetSelector = (net.minecraft.world.entity.ai.goal.GoalSelector) field.get(skeleton);

            var targetTypeField = net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal.class
                    .getDeclaredField("targetType");
            targetTypeField.setAccessible(true);

            return targetSelector.getAvailableGoals().stream()
                    .map(net.minecraft.world.entity.ai.goal.WrappedGoal::getGoal)
                    .filter(net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal.class::isInstance)
                    .anyMatch(goal -> {
                        try {
                            return targetTypeField.get(goal) == net.minecraft.world.entity.monster.Monster.class;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Chance-based (3-10% per call): loops calling the hook enough times that a false negative is
     * statistically negligible, rather than asserting after a single call.
     */
    public static void geodeGraceEventuallyConvertsStoneToOre(GameTestHelper helper) {
        var killer = helper.makeMockPlayer(GameType.SURVIVAL);
        boolean anyOreSpawned = false;

        for (int i = 0; i < 300 && !anyOreSpawned; i++) {
            BlockPos pos = ORIGIN.offset(0, 0, 0);
            helper.setBlock(pos, Blocks.STONE);
            Pig victim = helper.spawn(EntityType.PIG, pos);
            victim.setHealth(0F);
            victim.addEffect(new MobEffectInstance(MobEffects.GEODE_GRACE, 200, 0));

            GeodeGraceEffect.onEntityDeath(victim, killer);
            anyOreSpawned = !helper.getBlockState(pos).is(Blocks.STONE);
            victim.discard();
        }

        assertTrue(helper, anyOreSpawned, "no ore spawned after 300 kills with geode_grace active");
        helper.succeed();
    }

    /** Void damage teleports the holder back to the top of the world and negates the damage. */
    public static void fallOfTheVoidRescuesTheHolder(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        holder.addEffect(new MobEffectInstance(MobEffects.FALL_OF_THE_VOID, 200, 0));

        DamageSource voidDamage = holder.damageSources().fellOutOfWorld();
        float resultingDamage = FallOfTheVoidEffect.onLivingEntityDamage(holder, voidDamage, 20F);

        assertTrue(helper, resultingDamage == 0F, "void damage was not negated: " + resultingDamage);
        assertTrue(helper, holder.getY() >= level.getMaxY(), "the holder was not teleported to the top of the world");
        assertTrue(helper, holder.hasEffect(net.minecraft.world.effect.MobEffects.SLOW_FALLING),
                "the holder did not gain slow falling after being rescued");
        helper.succeed();
    }

    /** Redirects damage away from the holder and onto their soul mate. */
    public static void soulMateRedirectsDamageToThePairedEntity(GameTestHelper helper) {
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        Pig mate = helper.spawn(EntityType.PIG, ORIGIN.offset(2, 0, 0));
        holder.setHealth(holder.getMaxHealth());
        mate.setHealth(mate.getMaxHealth());

        holder.addEffect(new MobEffectInstance(MobEffects.SOUL_MATE, 200, 3));
        SoulMateEffect.onPotionAdded(holder);
        SoulMateEffect.onPotionAdded(mate);

        float mateHealthBefore = mate.getHealth();
        DamageSource damageSource = holder.damageSources().generic();
        float resultingDamage = SoulMateEffect.onEntityHurt(holder, damageSource, 10F);

        assertTrue(helper, resultingDamage < 10F, "no damage was redirected away from the holder");
        assertTrue(helper, mate.getHealth() < mateHealthBefore,
                "the soul mate took none of the redirected damage");

        SoulMateEffect.onPotionExpired(holder);
        SoulMateEffect.onPotionExpired(mate);
        helper.succeed();
    }

    /** Tracks players holding the effect so the server-wide tick rate can be recomputed from them. */
    public static void flyingTimeTracksHoldersByUuid(GameTestHelper helper) {
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        MobEffectInstance instance = new MobEffectInstance(MobEffects.FLYING_TIME, 200, 2);

        FlyingTimeEffect.onPotionAdded(holder, instance);
        assertTrue(helper, FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.get(holder.getUUID()) != null
                        && FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.get(holder.getUUID()) == 2,
                "flying_time did not record the holder's amplifier");

        FlyingTimeEffect.onPotionExpired(holder, instance);
        assertTrue(helper, !FlyingTimeEffect.FLYING_TIME_EFFECT_PLAYERS.containsKey(holder.getUUID()),
                "flying_time did not forget the holder once the effect expired");
        helper.succeed();
    }

    // ==================== physics ====================

    /** An airborne entity with the effect loses horizontal speed slower than one without it. */
    public static void slipNSlideReducesAirFrictionOnLanding(GameTestHelper helper) {
        Pig withEffect = helper.spawn(EntityType.PIG, ORIGIN);
        Pig withoutEffect = helper.spawn(EntityType.PIG, ORIGIN.offset(3, 0, 0));
        withEffect.addEffect(new MobEffectInstance(MobEffects.SLIP_N_SLIDE, 200, 0));

        withEffect.setDeltaMovement(0.3, 0, 0);
        withoutEffect.setDeltaMovement(0.3, 0, 0);
        withEffect.setOnGround(true);
        withoutEffect.setOnGround(true);

        for (int i = 0; i < 5; i++) {
            withEffect.travel(net.minecraft.world.phys.Vec3.ZERO);
            withoutEffect.travel(net.minecraft.world.phys.Vec3.ZERO);
        }

        assertTrue(helper,
                withEffect.getDeltaMovement().horizontalDistance() > withoutEffect.getDeltaMovement().horizontalDistance(),
                "slip_n_slide did not preserve more horizontal speed than an entity without it");
        helper.succeed();
    }

    /** A downward fall is turned into an upward bounce, scaled by amplifier. */
    public static void bouncingReversesDownwardVelocityOnFall(GameTestHelper helper) {
        Pig holder = helper.spawn(EntityType.PIG, ORIGIN);
        holder.addEffect(new MobEffectInstance(MobEffects.BOUNCING, 200, 0));
        holder.setDeltaMovement(0, -0.8, 0);

        boolean bounced = BouncingEffect.onFall(holder);

        assertTrue(helper, bounced, "onFall reported no bounce for a holder with the effect");
        assertTrue(helper, holder.getDeltaMovement().y > 0,
                "downward velocity was not reversed into an upward bounce: " + holder.getDeltaMovement().y);
        helper.succeed();
    }

    // ----- helpers -----

    private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            throw helper.assertionException(Component.literal(message));
        }
    }
}

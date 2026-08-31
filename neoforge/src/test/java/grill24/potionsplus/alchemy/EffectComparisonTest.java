package grill24.potionsplus.alchemy;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the 2.4 "identityString / Identifier-path" trap: the slug produced by
 * {@link EffectComparison#identitySlug(ItemStack)} must always be a legal
 * {@code ResourceLocation} path ({@code [a-z0-9_./-]}), because it is used to
 * build recipe and advancement ids. This is the case that surfaced as a game-test
 * crash on 26.1.2 rather than a unit-test failure.
 */
class EffectComparisonTest {
    private static final Pattern RESOURCE_LOCATION_PATH = Pattern.compile("[a-z0-9_./-]+");

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void identitySlugIsAlwaysAResourceLocationPath() {
        for (ItemStack stack : sampleStacks()) {
            String slug = EffectComparison.identitySlug(stack);
            assertTrue(RESOURCE_LOCATION_PATH.matcher(slug).matches(),
                    "identitySlug produced a non-path-safe id: '" + slug + "' for " + stack);
        }
    }

    private static List<ItemStack> sampleStacks() {
        return List.of(
                new ItemStack(Items.POTION),
                PotionContents.createItemStack(Items.POTION, Potions.NIGHT_VISION),
                PotionContents.createItemStack(Items.SPLASH_POTION, Potions.STRONG_HEALING),
                PotionContents.createItemStack(Items.LINGERING_POTION, Potions.LONG_SLOWNESS),
                PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.POISON),
                potionWithEffects(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2)),
                potionWithEffects(
                        new MobEffectInstance(MobEffects.JUMP, 6000, 4),
                        new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)
                ),
                new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(Items.APPLE)
        );
    }

    private static ItemStack potionWithEffects(MobEffectInstance... effects) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effects)));
        return stack;
    }
}

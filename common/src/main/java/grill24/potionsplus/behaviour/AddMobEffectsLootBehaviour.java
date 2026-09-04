package grill24.potionsplus.behaviour;

import grill24.potionsplus.alchemy.PotionContainer;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader-agnostic core of the "add passive mob effects" global loot modifier: rolls a chance to graft
 * one to three random passive potion effects onto damageable, non-potion loot. Each loader's
 * global-loot-modifier implementation wraps this, supplying the eligible-effects pool (see
 * {@code EffectRegistry.passiveEligible}).
 */
public class AddMobEffectsLootBehaviour {
    public static List<ItemStack> apply(List<ItemStack> generatedLoot, RandomSource random, List<Holder<MobEffect>> eligibleEffects) {
        List<ItemStack> modifiedLoot = new ArrayList<>();
        for (ItemStack stack : generatedLoot) {
            ItemStack modifiedStack = stack.copy();
            if (isItemEligibleForPassivePotionEffects(stack) && random.nextFloat() < 0.3F) {
                int numEffects = (int) Math.round(Math.clamp(Utility.nextGaussian(1.25F, 0.5F, random), 1, 3));
                for (int i = 0; i < numEffects; i++) {
                    modifiedStack = addRandomPassivePotionEffect(random, modifiedStack, eligibleEffects);
                }
            }
            modifiedLoot.add(modifiedStack);
        }
        return modifiedLoot;
    }

    private static boolean isItemEligibleForPassivePotionEffects(ItemStack stack) {
        return stack.isDamageableItem() && !PotionContainer.isPotionStack(stack);
    }

    private static ItemStack addRandomPassivePotionEffect(RandomSource random, ItemStack stack, List<Holder<MobEffect>> eligibleEffects) {
        if (eligibleEffects.isEmpty()) {
            return stack;
        }
        Holder<MobEffect> effect = eligibleEffects.get(random.nextInt(eligibleEffects.size()));

        int amplifier = (int) Math.round(Math.clamp(Utility.nextGaussian(1, 1, random), 1F, 3F));
        int duration = random.nextInt(4800) + 300;
        MobEffectInstance effectInstance = new MobEffectInstance(effect, duration, amplifier);

        List<MobEffectInstance> customEffects = new ArrayList<>(PotionData.read(stack).effects());
        customEffects.add(effectInstance);
        return PotionDataBuilder.from(stack).withEffects(customEffects).applyTo(stack);
    }
}

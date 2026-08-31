package grill24.potionsplus.alchemy;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cached metadata about the mob-effect registry: the icon order used by the
 * effect-icon model, and a pre-built pool of effects eligible for passive
 * application (excluding the {@code any_potion}/{@code any_other_potion}
 * sentinels).
 * <p>
 * All caches are built lazily on first use, which occurs after the mob-effect
 * registry is frozen, so there is no static-initialization hazard with
 * {@link MobEffects}.
 */
public final class EffectRegistry {
    private EffectRegistry() {
    }

    private static List<MobEffect> allMobEffects;
    private static Map<ResourceLocation, Integer> iconStackSizeMap;
    private static List<Holder.Reference<MobEffect>> passivePool;

    /**
     * Effects of the {@code minecraft} and {@code potionsplus} namespaces, in a
     * stable (sorted-by-key) order. Cached once; the current sorted order is
     * load-bearing for the effect-icon model overrides, so it is intentionally
     * preserved here.
     */
    public static List<MobEffect> getAllMobEffects() {
        if (allMobEffects == null) {
            List<MobEffect> effects = new ArrayList<>();
            for (Map.Entry<ResourceKey<MobEffect>, MobEffect> value : BuiltInRegistries.MOB_EFFECT.entrySet()) {
                if (value.getKey().location().getNamespace().equals("minecraft") || value.getKey().location().getNamespace().equals(ModInfo.MOD_ID)) {
                    effects.add(value.getValue());
                }
            }
            effects.sort(Comparator.comparing(BuiltInRegistries.MOB_EFFECT::getKey));
            allMobEffects = effects;
        }
        return allMobEffects;
    }

    public static Map<ResourceLocation, Integer> getIconStackSizeMap() {
        if (iconStackSizeMap == null) {
            Map<ResourceLocation, Integer> map = new HashMap<>();
            int i = 0;
            for (MobEffect value : getAllMobEffects()) {
                i++;
                map.put(BuiltInRegistries.MOB_EFFECT.getKey(value), i);
            }
            iconStackSizeMap = map;
        }
        return iconStackSizeMap;
    }

    private static List<Holder.Reference<MobEffect>> getPassivePool() {
        if (passivePool == null) {
            ResourceKey<MobEffect> anyPotion = MobEffects.ANY_POTION.getKey();
            ResourceKey<MobEffect> anyOtherPotion = MobEffects.ANY_OTHER_POTION.getKey();
            passivePool = BuiltInRegistries.MOB_EFFECT.holders()
                    .filter(holder -> !holder.getKey().equals(anyPotion) && !holder.getKey().equals(anyOtherPotion))
                    .toList();
        }
        return passivePool;
    }

    /**
     * Adds a random passive potion effect to a damageable, non-potion item stack,
     * drawn from the pre-built passive pool (excluding the {@code any_potion} /
     * {@code any_other_potion} sentinels) and honoring the caller's blacklist.
     * Ported from {@code PUtil.addRandomPassivePotionEffect}.
     */
    public static void addRandomPassivePotionEffect(LootContext context, ItemStack stack, Set<ResourceKey<MobEffect>> excludedEffects) {
        if (!PotionContainer.isItemEligibleForPassivePotionEffects(stack)) {
            return;
        }
        List<Holder.Reference<MobEffect>> pool = getPassivePool();
        if (pool.isEmpty()) {
            return;
        }

        RandomSource random = context.getRandom();
        Holder.Reference<MobEffect> holder = pool.get(random.nextInt(pool.size()));
        int attempts = 0;
        while (holder != null && excludedEffects.contains(holder.getKey()) && attempts < 3) {
            holder = pool.get(random.nextInt(pool.size()));
            attempts++;
        }
        if (holder == null || excludedEffects.contains(holder.getKey())) {
            return;
        }

        List<MobEffectInstance> customEffects = new ArrayList<>(PotionData.getAllEffects(stack));
        int amplifier = (int) Math.round(Math.clamp(Utility.nextGaussian(1, 1, random), 1F, 3F));
        int duration = random.nextInt(4800) + 300;
        customEffects.add(new MobEffectInstance(holder, duration, amplifier));
        PotionDataBuilder.setCustomEffects(stack, customEffects);
    }
}

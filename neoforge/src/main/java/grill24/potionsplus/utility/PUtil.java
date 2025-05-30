package grill24.potionsplus.utility;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nonnull;
import java.util.*;

public class PUtil {

    public static final String POTION_PREFIX = "Potion of ";
    public static final String SPLASH_POTION_PREFIX = "Splash Potion of ";
    public static final String LINGERING_POTION_PREFIX = "Lingering Potion of ";
    public static final String TIPPED_ARROW_PREFIX = "Arrow of ";

    public static ItemStack createPotionItemStack(Holder<Potion> potionHolder, PotionType type, int count) {
        ItemStack potionItem = switch (type) {
            case POTION -> PotionContents.createItemStack(Items.POTION, potionHolder);
            case SPLASH_POTION -> PotionContents.createItemStack(Items.SPLASH_POTION, potionHolder);
            case LINGERING_POTION -> PotionContents.createItemStack(Items.LINGERING_POTION, potionHolder);
            case TIPPED_ARROW -> PotionContents.createItemStack(Items.TIPPED_ARROW, potionHolder);
        };
        potionItem.setCount(count);
        return potionItem;
    }

    public static ItemStack createPotionItemStack(Holder<Potion> potion, PotionType type) {
        return createPotionItemStack(potion, type, 1);
    }

    public static boolean isSameItemOrPotion(ItemStack itemStack, ItemStack other, List<BrewingCauldronRecipe.PotionMatchingCriteria> matchingCriteria) {
        boolean shouldNeverMatch = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.NEVER_MATCH);
        if (shouldNeverMatch) {
            return false;
        }
        boolean shouldIgnorePotionContainer = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER);
        boolean requiresExactMatch = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.EXACT_MATCH);
        boolean shouldIgnorePotionEffects = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS);
        boolean requiresMinimumOneEffect = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT);
        boolean shouldIgnorePotionEffectAmplifier = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECT_AMPLIFIER);
        boolean shouldIgnorePotionEffectDuration = matchingCriteria.contains(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECT_DURATION);

        boolean isSameContainer = shouldIgnorePotionContainer || ItemStack.isSameItem(itemStack, other);
        if (PUtil.isPotion(itemStack) && PUtil.isPotion(other) && isSameContainer) {
            PotionContents potionContents = getPotionContents(itemStack);
            PotionContents otherPotionContents = getPotionContents(other);

            // Exact match ez
            if (requiresExactMatch) {
                return ItemStack.isSameItemSameComponents(itemStack, other);
            }

            // Ignore potion effects but require at least one effect of any type
            if (requiresMinimumOneEffect) {
                return potionContents.hasEffects() && otherPotionContents.hasEffects();
            }

            // Ignore potion effects - only check the potion container
            if (shouldIgnorePotionEffects) {
                return true;
            }

            // Check linked Potion in PotionContents
            if (!potionContents.potion().equals(otherPotionContents.potion())) {
                return false;
            }

            // Check potion effects for matching duration and/or amplifier depending on the matching criteria
            List<MobEffectInstance> effects = PUtil.getAllEffects(potionContents);
            List<MobEffectInstance> otherEffects = PUtil.getAllEffects(otherPotionContents);
            if (effects.size() != otherEffects.size()) {
                return false;
            }
            for (int i = 0; i < effects.size(); i++) {
                MobEffectInstance effect = effects.get(i);
                MobEffectInstance otherEffect = otherEffects.get(i);
                if (!effect.getEffect().equals(otherEffect.getEffect())
                        || (effect.getAmplifier() != otherEffect.getAmplifier() && shouldIgnorePotionEffectAmplifier)
                        || (effect.getDuration() != otherEffect.getDuration() && shouldIgnorePotionEffectDuration)) {
                    return false;
                }
            }
            // If we reach here, the potion effects are the same
            return true;
        } else {
            return ItemStack.isSameItemSameComponents(itemStack, other);
        }
    }

    public static List<MobEffectInstance> getAllEffects(@Nonnull PotionContents potionContents) {
        List<MobEffectInstance> allEffects = new ArrayList<>();
        potionContents.getAllEffects().forEach(allEffects::add);
        return allEffects;
    }

    public static List<MobEffectInstance> getAllEffects(@Nonnull ItemStack itemStack) {
        if (itemStack.has(DataComponents.POTION_CONTENTS)) {
            return getAllEffects(getPotionContents(itemStack));
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isPotion(ItemStack itemStack) {
        return itemStack.getItem() == Items.POTION || itemStack.getItem() == Items.SPLASH_POTION || itemStack.getItem() == Items.LINGERING_POTION || itemStack.getItem() == Items.TIPPED_ARROW;
    }

    public static String getNameOrVerbosePotionName(ItemStack itemStack) {
        if (isPotion(itemStack)) {
            StringBuilder name = new StringBuilder();
            if(PUtil.hasPotion(itemStack)) {
                name.append(PUtil.getPotionHolder(itemStack).getKey().location().getPath()).append("_");
            }
            for (MobEffectInstance mobEffectInstance : PUtil.getAllEffects(itemStack)) {
                name.append(mobEffectInstance.getEffect().getKey().location().getPath()).append("_")
                        .append("a").append(mobEffectInstance.getAmplifier()).append("_")
                        .append("d").append(mobEffectInstance.getDuration()).append("_");
            }
            name.append(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath());
            return name.toString();
        } else {
            return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        }
    }

    public static boolean isPotionsPlusPotion(ItemStack itemStack) {
        return isPotion(itemStack) && BuiltInRegistries.POTION.getKey(getPotion(itemStack)).getNamespace().equals(ModInfo.MOD_ID);
    }

    // TODO: Not rely on potion.
    @Deprecated
    public static int getProcessingTime(int baseTime, ItemStack input, ItemStack output, int numNonPotionIngredients) {
        int processingTime = baseTime;

        if (isPotion(output)) {
            Potion potion = getPotion(output);

            if (potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.MUNDANE) {
                processingTime = (int) (processingTime * 0.5);
                if (potion == Potions.THICK || potion == Potions.MUNDANE) {
                    // Brewing Cauldron uses processing time as priority for recipe selection
                    // Thick and mundane potions are rarely made and should not take priority over awkward potion crafting
                    processingTime -= 1;
                }
            }

            if (input.getItem().equals(Items.SPLASH_POTION)) {
                processingTime = (int) (processingTime * 1.5);
            } else if (input.getItem().equals(Items.LINGERING_POTION)) {
                processingTime = processingTime * 2;
            }
        }

        processingTime += (int) (processingTime * (numNonPotionIngredients - 1) * 0.25f);
        return processingTime;
    }

    public static String getPotionName(PotionType type, String potionName) {
        return switch (type) {
            case POTION -> POTION_PREFIX + potionName;
            case SPLASH_POTION -> SPLASH_POTION_PREFIX + potionName;
            case LINGERING_POTION -> LINGERING_POTION_PREFIX + potionName;
            case TIPPED_ARROW -> TIPPED_ARROW_PREFIX + potionName;
        };
    }

    // Potions
    public enum PotionType {
        POTION,
        SPLASH_POTION,
        LINGERING_POTION,
        TIPPED_ARROW
    }

    public static List<MobEffect> getAllMobEffects() {
        List<MobEffect> effects = new ArrayList<>();
        for (Map.Entry<ResourceKey<MobEffect>, MobEffect> value : BuiltInRegistries.MOB_EFFECT.entrySet()) {
            if (value.getKey().location().getNamespace().equals("minecraft") || value.getKey().location().getNamespace().equals(ModInfo.MOD_ID)) {
                effects.add(value.getValue());
            }
        }

        // Sort by name for consistency
        // We use this list to map from overrides in the Potion Effect Icon model
        // Funky logic, ik
        effects.sort(Comparator.comparing(BuiltInRegistries.MOB_EFFECT::getKey));
        return effects;
    }

    public static Map<ResourceLocation, Integer> getAllMobEffectsIconStackSizeMap() {
        Map<ResourceLocation, Integer> effects = new HashMap<>();
        int i = 0;
        for (MobEffect value : getAllMobEffects()) {
            ;
            i++;
            effects.put(BuiltInRegistries.MOB_EFFECT.getKey(value), i);
        }
        return effects;
    }


    // ----- 1.21 -----

    public static boolean hasPotion(ItemStack itemStack) {
        if (itemStack.has(DataComponents.POTION_CONTENTS)) {
            return itemStack.get(DataComponents.POTION_CONTENTS).potion().isPresent();
        }
        return false;
    }

    public static Potion getPotion(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            Optional<Holder<Potion>> potion = getPotionContents(itemStack).potion();

            return potion.map(Holder::value).orElse(Potions.WATER.value());
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            return itemStack.get(DataComponents.POTION_CONTENTS).potion().orElse(null);
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static Holder<Potion> getPotionHolder(Potion potion) {
        return BuiltInRegistries.POTION.getHolder(BuiltInRegistries.POTION.getKey(potion)).orElseThrow();
    }

    public static PotionContents getPotionContents(ItemStack itemStack) {
        if(itemStack.has(DataComponents.POTION_CONTENTS)) {
            return itemStack.get(DataComponents.POTION_CONTENTS);
        }

        throw new IllegalArgumentException("ItemStack does not have potion contents");
    }

    public static ItemStack setCustomEffects(ItemStack itemStack, List<MobEffectInstance> customEffects) {
        PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), Collections.emptyList()));
        PotionContents newPotionContents = new PotionContents(potionContents.potion(), potionContents.customColor(), customEffects);
        itemStack.set(DataComponents.POTION_CONTENTS, newPotionContents);
        return itemStack;
    }

    public static PotionUpgradeIngredients.Rarity getRarity(PpIngredient ingredient) {
        if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, ingredient)) {
            return PotionUpgradeIngredients.Rarity.COMMON;
        } else if (SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, ingredient)) {
            return PotionUpgradeIngredients.Rarity.RARE;
        }

        return PotionUpgradeIngredients.Rarity.NONE;
    }

    public static boolean isPassivePotionEffectItem(ItemStack itemStack) {
        return itemStack.isDamageableItem() && itemStack.has(DataComponents.POTION_CONTENTS) && !PUtil.isPotion(itemStack);
    }

    public static boolean isItemEligibleForPassivePotionEffects(ItemStack itemStack) {
        return itemStack.isDamageableItem() && !PUtil.isPotion(itemStack);
    }

    /**
     * Used by loot modifier {@link grill24.potionsplus.behaviour.AddMobEffectsLootModifier} to add
     * random passive potion effects to an item stack.
     * @param context the loot context
     * @param stack the item stack to add the effects to
     */
    public static void addRandomPassivePotionEffect(LootContext context, ItemStack stack, Set<ResourceKey<MobEffect>> excludedEffects) {
        if (isItemEligibleForPassivePotionEffects(stack)) {
            Registry<MobEffect> mobEffectRegistry = context.getLevel().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
            Optional<Holder.Reference<MobEffect>> optionalHolder = mobEffectRegistry.getRandom(context.getRandom());
            int attempts = 0;
            while (optionalHolder.isPresent() && excludedEffects.contains(optionalHolder.get().getKey()) && attempts < 3) {
                optionalHolder = mobEffectRegistry.getRandom(context.getRandom());
                attempts++;
            }

            if (optionalHolder.isPresent() && !excludedEffects.contains(optionalHolder.get().getKey())) {
                List<MobEffectInstance> customEffects = new ArrayList<>(PUtil.getAllEffects(stack));
                int amplifier = (int) Math.round(Math.clamp(Utility.nextGaussian(1, 1, context.getRandom()), 1F, 3F));
                int duration = context.getRandom().nextInt(4800) + 300;
                MobEffectInstance effectInstance = new MobEffectInstance(optionalHolder.get(), duration, amplifier);
                customEffects.add(effectInstance);
                setCustomEffects(stack, customEffects);
            }
        }
    }

    public static List<ItemStack> getDisplayStacksForJeiRecipe(ItemStack itemStack) {
        if (PUtil.isPotion(itemStack)) {
            // TODO: Add potions HERE
            boolean isAnyPotion = PUtil.getAllEffects(itemStack).stream().anyMatch(instance -> instance.getEffect().is(MobEffects.ANY_POTION) || instance.getEffect().is(MobEffects.ANY_OTHER_POTION));
            if(isAnyPotion) {
                return new ArrayList<>(BuiltInRegistries.POTION.holders().map(potion -> PUtil.createPotionItemStack(potion, PotionType.POTION)).toList());
            }
        }

        return Collections.singletonList(itemStack);
    }

    public static float diminishingReturnsLn(float amplifier) {
        return (float) Math.log(amplifier + 1) + 1;
    }

    public static float diminishingReturns(float amplifier, float horizontalAsymptote) {
        if(horizontalAsymptote < 0) {
            throw new IllegalArgumentException("Horizontal asymptote must be greater than or equal to 0");
        }
        return 2 * (horizontalAsymptote * amplifier) / (horizontalAsymptote + amplifier);
    }
}

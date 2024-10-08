package grill24.potionsplus.utility;

import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

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

    public static List<MobEffectInstance> getAllEffects(PotionContents potionContents) {
        List<MobEffectInstance> allEffects = new ArrayList<>();
        for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
            allEffects.add(mobEffectInstance);
        }
        return allEffects;
    }

    public static boolean isPotion(ItemStack itemStack) {
        return itemStack.getItem() == Items.POTION || itemStack.getItem() == Items.SPLASH_POTION || itemStack.getItem() == Items.LINGERING_POTION || itemStack.getItem() == Items.TIPPED_ARROW;
    }

    public static String getNameOrVerbosePotionName(ItemStack itemStack) {
        if (isPotion(itemStack)) {
            return BuiltInRegistries.POTION.getKey(getPotion(itemStack)).getPath() + "_" + BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
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
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        PotionContents newPotionContents = new PotionContents(potionContents.potion(), potionContents.customColor(), customEffects);
        itemStack.set(DataComponents.POTION_CONTENTS, newPotionContents);
        return itemStack;
    }

    public static String getUniqueRecipeName(List<PpIngredient> ingredients, ItemStack result) {
        StringBuilder name = new StringBuilder();
        for (PpIngredient ingredient : ingredients) {
            name.append(getNameOrVerbosePotionName(ingredient.getItemStack())).append("_");
        }
        name.append("to_");
        name.append(getNameOrVerbosePotionName(result));
        return name.toString();
    }
}

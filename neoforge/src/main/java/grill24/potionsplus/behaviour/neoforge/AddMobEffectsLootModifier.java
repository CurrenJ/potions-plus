package grill24.potionsplus.behaviour.neoforge;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.neoforge.LootModifiers;
import grill24.potionsplus.alchemy.*;
import grill24.potionsplus.utility.Utility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AddMobEffectsLootModifier extends LootModifier {
    public static final Supplier<MapCodec<AddMobEffectsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .and(NeoForgeExtraCodecs.setOf(ResourceKey.codec(Registries.MOB_EFFECT)).optionalFieldOf("blacklistedEffects", Set.of()).forGetter(m -> m.blacklistedEffects))
            .apply(inst, AddMobEffectsLootModifier::new)
    ));

    private final Set<ResourceKey<MobEffect>> blacklistedEffects;
    private final Supplier<List<Holder<MobEffect>>> eligibleEffects;

    public AddMobEffectsLootModifier(LootItemCondition[] conditionsIn, Set<ResourceKey<MobEffect>> blacklistedEffects) {
        super(conditionsIn);
        this.blacklistedEffects = blacklistedEffects;
        this.eligibleEffects = Suppliers.memoize(() -> EffectRegistry.passiveEligible(blacklistedEffects));
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> modifiedLoot = new ObjectArrayList<>();
        for (ItemStack stack : generatedLoot) {
            ItemStack modifiedStack = stack.copy();
            if (isItemEligibleForPassivePotionEffects(stack) && context.getRandom().nextFloat() < 0.3F) {
                int numEffects = (int) Math.round(Math.clamp(Utility.nextGaussian(1.25F, 0.5F, context.getRandom()), 1, 3));
                for (int i = 0; i < numEffects; i++) {
                    modifiedStack = addRandomPassivePotionEffect(context, modifiedStack, eligibleEffects.get());
                }
            }
            modifiedLoot.add(modifiedStack);
        }

        return modifiedLoot;
    }

    private static boolean isItemEligibleForPassivePotionEffects(ItemStack stack) {
        return stack.isDamageableItem() && !PotionContainer.isPotionStack(stack);
    }

    private static ItemStack addRandomPassivePotionEffect(
            LootContext context, ItemStack stack, List<Holder<MobEffect>> eligibleEffects) {
        if (eligibleEffects.isEmpty()) {
            return stack;
        }
        Holder<MobEffect> effect = eligibleEffects.get(context.getRandom().nextInt(eligibleEffects.size()));

        int amplifier = (int) Math.round(Math.clamp(Utility.nextGaussian(1, 1, context.getRandom()), 1F, 3F));
        int duration = context.getRandom().nextInt(4800) + 300;
        MobEffectInstance effectInstance = new MobEffectInstance(effect, duration, amplifier);

        List<MobEffectInstance> customEffects = new ArrayList<>(PotionData.read(stack).effects());
        customEffects.add(effectInstance);
        return PotionDataBuilder.from(stack).withEffects(customEffects).applyTo(stack);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return LootModifiers.ADD_MOB_EFFECTS.value();
    }
}

package grill24.potionsplus.behaviour;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.LootModifiers;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.StreamCodecUtility;
import grill24.potionsplus.utility.Utility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class AddMobEffectsLootModifier extends LootModifier {
    public static final Supplier<MapCodec<AddMobEffectsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
            .and(NeoForgeExtraCodecs.setOf(ResourceKey.codec(Registries.MOB_EFFECT)).optionalFieldOf("blacklistedEffects", Set.of()).forGetter(m -> m.blacklistedEffects))
            .apply(inst, AddMobEffectsLootModifier::new)
    ));

    private final Set<ResourceKey<MobEffect>> blacklistedEffects;
    public AddMobEffectsLootModifier(LootItemCondition[] conditionsIn, Set<ResourceKey<MobEffect>> blacklistedEffects) {
        super(conditionsIn);
        this.blacklistedEffects = blacklistedEffects;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> modifiedLoot = new ObjectArrayList<>();
        for (ItemStack stack : generatedLoot) {
            ItemStack modifiedStack = stack.copy();
            if (PUtil.isItemEligibleForPassivePotionEffects(stack) && context.getRandom().nextFloat() < 0.3F) {
                int numEffects = (int) Math.round(Math.clamp(Utility.nextGaussian(1.25F, 0.5F, context.getRandom()), 1, 3));
                for (int i = 0; i < numEffects; i++) {
                    PUtil.addRandomPassivePotionEffect(context, modifiedStack, blacklistedEffects);
                }
            }
            modifiedLoot.add(modifiedStack);
        }

        return modifiedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return LootModifiers.ADD_MOB_EFFECTS.value();
    }
}

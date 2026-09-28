package grill24.potionsplus.core.fabric;

import grill24.potionsplus.alchemy.EffectRegistry;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.Utility;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fabric equivalent of the NeoForge/Forge global loot modifiers (Wormroot, AddMobEffects). Fabric has no
 * unified GLM system, so this hooks {@code LootTableEvents.MODIFY_DROPS}, which fires post-generation
 * with the same mutable drops list a GLM's {@code doApply} receives.
 */
public final class LootModifiers {
    private static final List<Block> WORMROOT_BLOCKS = List.of(Blocks.HANGING_ROOTS, Blocks.ROOTED_DIRT);

    private LootModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY_DROPS.register(LootModifiers::applyWormroot);
        LootTableEvents.MODIFY_DROPS.register(LootModifiers::applyMobEffects);
    }

    private static void applyWormroot(Holder<LootTable> table, LootContext context, List<ItemStack> drops) {
        if (!context.hasParameter(LootContextParams.BLOCK_STATE)) {
            return;
        }
        Block block = context.getOptionalParameter(LootContextParams.BLOCK_STATE).getBlock();
        for (Block b : WORMROOT_BLOCKS) {
            if (block != b) {
                continue;
            }
            if (context.getRandom().nextInt(4) == 0) {
                if (block == Blocks.ROOTED_DIRT) {
                    drops.add(new ItemStack(net.minecraft.world.item.Items.DIRT, 1));
                }
                drops.removeIf(stack -> Block.byItem(stack.getItem()) == b);
                drops.add(new ItemStack(BrewingItems.WORMROOT.value(), 1));
            }
            break;
        }
    }

    private static final Set<net.minecraft.resources.ResourceKey<MobEffect>> BLACKLISTED_EFFECTS = computeBlacklist();
    private static List<Holder<MobEffect>> eligibleEffects;

    private static Set<net.minecraft.resources.ResourceKey<MobEffect>> computeBlacklist() {
        Set<net.minecraft.resources.ResourceKey<MobEffect>> blacklist = new HashSet<>();
        BuiltInRegistries.MOB_EFFECT.entrySet().forEach(entry -> {
            MobEffect effect = entry.getValue();
            if (effect.isInstantenous() || effect.getCategory() == MobEffectCategory.HARMFUL) {
                blacklist.add(entry.getKey());
            }
        });
        blacklist.add(MobEffects.ANY_POTION.unwrapKey().orElseThrow());
        blacklist.add(MobEffects.ANY_OTHER_POTION.unwrapKey().orElseThrow());
        return blacklist;
    }

    private static void applyMobEffects(Holder<LootTable> table, LootContext context, List<ItemStack> drops) {
        if (eligibleEffects == null) {
            eligibleEffects = EffectRegistry.passiveEligible(BLACKLISTED_EFFECTS);
        }

        for (int i = 0; i < drops.size(); i++) {
            ItemStack stack = drops.get(i);
            if (Utility.isItemEligibleForPassivePotionEffects(stack) && context.getRandom().nextFloat() < 0.3F) {
                ItemStack modifiedStack = stack.copy();
                int numEffects = (int) Math.round(Math.clamp(Utility.nextGaussian(1.25F, 0.5F, context.getRandom()), 1, 3));
                for (int j = 0; j < numEffects; j++) {
                    modifiedStack = addRandomPassivePotionEffect(context, modifiedStack);
                }
                drops.set(i, modifiedStack);
            }
        }
    }

    private static ItemStack addRandomPassivePotionEffect(LootContext context, ItemStack stack) {
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
}

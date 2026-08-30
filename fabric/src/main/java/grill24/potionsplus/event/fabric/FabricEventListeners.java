package grill24.potionsplus.event.fabric;

import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import grill24.potionsplus.command.PpCommands;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.core.Entities;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.network.ClientboundSyncPairedAbyssalTrove;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.platform.PacketNetwork;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ServerTickHandler;
import grill24.potionsplus.utility.TickHandler;
import grill24.potionsplus.utility.Utility;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric equivalents of the NeoForge server-side event listeners, registered as fabric-api
 * callbacks from {@code PotionsPlusFabric.onInitialize()}. The mixin-based listeners (effects,
 * enchantment level, advancement earn, item pickup) live in {@code grill24.potionsplus.mixin.fabric}.
 */
public final class FabricEventListeners {

    private FabricEventListeners() {
    }

    public static void register() {
        registerCommands();
        registerTicks();
        registerDeathAndAttributes();
        registerInteractions();
        registerPotionStackSize();
    }

    // ----- NeoCommandEvents -----

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, ctx, selection) -> PpCommands.register(dispatcher));
    }

    // ----- NeoDelayedEvents + NeoServerTickEvents + PlayerListeners.onTick -----

    private static void registerTicks() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DelayedEvents.tick(TickHandler.ticks());
            ServerTickHandler.increment();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> applyAllPassiveItemPotionEffects(server.getPlayerList().getPlayers()));
    }

    // ----- EffectListeners.onEntityDeath + EntityListeners.onEntityDeath + attribute events -----

    private static void registerDeathAndAttributes() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            GeodeGraceEffect.onEntityDeath(entity, source.getEntity());
            SoulMateEffect.onEntityDeath(entity);
            Grungler.onEntityDeath(entity);
        });

        // EntityListeners.createDefaultAttributes (takes the Builder directly, unlike NeoForge's event.put(type, builder.build())).
        FabricDefaultAttributeRegistry.register(Entities.GRUNGLER.value(), Grungler.createAttributes());

        // NeoAttributeEvents (add PP attributes to players).
        FabricDefaultAttributeRegistry.MODIFY.register(context ->
                context.modify(EntityType.PLAYER, (type, builder) -> Attributes.getAllAttributes().forEach(builder::add)));
    }

    // ----- PlayerListeners.on (moss/clothesline) + onPlayerJoin + EntityListeners.onBreakBlock -----

    private static void registerInteractions() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            var pos = hitResult.getBlockPos();
            boolean mossHandled = MossBehaviour.doMossInteractions(level, pos, player.getItemInHand(hand), player, hand);
            boolean clotheslineHandled = ClotheslineBehaviour.doClotheslineInteractions(level, pos, player.getItemInHand(hand), player, hand);
            return (mossHandled || clotheslineHandled) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ServerPlayer player) {
                PacketNetwork.sendToPlayers(player,
                        new ClientboundSyncKnownBrewingRecipesPacket(SavedData.instance.getData(player).getKnownRecipeKeys()),
                        new CustomPacketPayload[]{new ClientboundSyncPairedAbyssalTrove(SavedData.instance.getData(player).getPairedAbyssalTrovePos())}
                );
            }
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!(level instanceof ServerLevel serverLevel)) {
                return false;
            }
            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, blockEntity);
            return Grungler.onBreakBlock(state, drops, player, pos);
        });
    }

    // ----- ItemListenersMod (potion stack size 16) -----

    private static void registerPotionStackSize() {
        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof PotionItem) {
                BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(entry.getKey(), (components, context, key) -> components.set(DataComponents.MAX_STACK_SIZE, 16));
            }
        }
    }

    // ----- Passive item potion effects (ported from NeoForge PlayerListeners) -----

    private static final int EFFECT_DURATION = 20 * 15; // Every 15 seconds
    private static int lastEffectActivation;

    private static void applyAllPassiveItemPotionEffects(List<ServerPlayer> players) {
        if (ServerTickHandler.ticksInGame > lastEffectActivation + EFFECT_DURATION) {
            lastEffectActivation = ServerTickHandler.ticksInGame;
            for (ServerPlayer player : players) {
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.MAINHAND);
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.OFFHAND);
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.HEAD);
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.CHEST);
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.LEGS);
                tryApplyPassiveItemPotionEffects(player, EquipmentSlot.FEET);
            }
        }
    }

    private static void tryApplyPassiveItemPotionEffects(Player player, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);
        if (Utility.isPassivePotionEffectItem(stack)) {
            List<MobEffectInstance> customEffects = new ArrayList<>();
            for (MobEffectInstance effect : PotionData.read(stack).effects()) {
                int durationApplied = Math.min(EFFECT_DURATION, effect.getDuration());
                MobEffectInstance e = new MobEffectInstance(effect.getEffect(), durationApplied, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), false);

                // Damage the item, but don't break it.
                int maxDamage = stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
                int damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
                int damageToApply = (e.getAmplifier() + 1) * 2;
                if (damage + damageToApply < maxDamage) {
                    player.addEffect(e);
                    stack.hurtAndBreak(e.getAmplifier() + 1, player, slot);
                }

                // Update the potion effects data on the item with the new duration
                int remainingDuration = effect.getDuration() - durationApplied;
                if (remainingDuration > 0) {
                    customEffects.add(new MobEffectInstance(effect.getEffect(), remainingDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), false));
                }
            }
            player.setItemSlot(slot, PotionDataBuilder.from(stack).withEffects(customEffects).applyTo(stack));
        }
    }
}

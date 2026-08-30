package grill24.potionsplus.event.forge;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.alchemy.PotionDataBuilder;
import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.command.PpCommands;
import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.core.Entities;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.network.ClientboundSyncPairedAbyssalTrove;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.platform.PacketNetwork;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ServerTickHandler;
import grill24.potionsplus.utility.TickHandler;
import grill24.potionsplus.utility.Utility;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Forge equivalents of the NeoForge server-side event listeners. Forge's eventbus 7 exposes a
 * static {@code BUS} on each event class; we register plain consumers (or predicates for the two
 * cancellable cases) against those.
 */
public final class ForgeEventListeners {

    private ForgeEventListeners() {
    }

    public static void register() {
        registerAdvancements();
        registerEffects();
        registerAttributes();
        registerCommands();
        registerTicks();
        registerPlayerListeners();
        registerPotionStackSize();
    }

    // ----- AdvancementListeners -----

    private static final Set<Identifier> ADVANCEMENTS_DROP_INGREDIENTS = Set.of(
            ppId("root"),
            ppId("create_abyssal_trove"),
            ppId("create_sanguine_altar")
    );

    private static void registerAdvancements() {
        AdvancementEvent.AdvancementEarnEvent.BUS.addListener((AdvancementEvent.AdvancementEarnEvent event) -> {
            if (ADVANCEMENTS_DROP_INGREDIENTS.contains(event.getAdvancement().id()) && event.getEntity() instanceof ServerPlayer player) {
                Set<PpIngredient> ingredients = Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients();
                PpIngredient ingredient = ingredients.stream().toList().get(player.level().getRandom().nextInt(ingredients.size()));
                ItemStack stack = ingredient.getItemStack().copy();
                if (!player.addItem(stack)) {
                    player.drop(stack, false);
                }
            }
        });
    }

    // ----- EffectListeners -----

    private static void registerEffects() {
        MobEffectEvent.Added.BUS.addListener((MobEffectEvent.Added event) -> {
            BoneBuddyEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            FlyingTimeEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            ExplodingEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionAdded(event.getEntity());
        });

        MobEffectEvent.Expired.BUS.addListener((MobEffectEvent.Expired event) -> {
            BoneBuddyEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
            FlyingTimeEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
            ExplodingEffect.onPotionExpiry(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionExpired(event.getEntity());
        });

        MobEffectEvent.Remove.BUS.addListener((MobEffectEvent.Remove event) -> {
            FlyingTimeEffect.onPotionRemoved(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionRemoved(event.getEntity());
        });

        // LivingFallEvent is cancellable via the predicate's boolean return.
        LivingFallEvent.BUS.addListener((LivingFallEvent event) -> {
            BouncingEffect.onFall(event.getEntity());
            return BouncingEffect.onLivingFall(event.getEntity(), (float) event.getDistance());
        });

        // Forge has a single LivingDamageEvent (not Pre/Post): getAmount()/setAmount().
        LivingDamageEvent.BUS.addListener((LivingDamageEvent event) -> {
            float damage = event.getAmount();

            float afterVoid = FallOfTheVoidEffect.onLivingEntityDamage(event.getEntity(), event.getSource(), damage);
            if (afterVoid != damage) {
                event.setAmount(afterVoid);
                damage = afterVoid;
            }

            float afterSoulMate = SoulMateEffect.onEntityHurt(event.getEntity(), event.getSource(), damage);
            if (afterSoulMate != damage) {
                event.setAmount(afterSoulMate);
            }
        });

        LivingHealEvent.BUS.addListener((LivingHealEvent event) -> {
            float newAmount = SoulMateEffect.onEntityHeal(event.getEntity(), event.getAmount());
            if (newAmount != event.getAmount()) {
                event.setAmount(newAmount);
            }
        });

        LivingDeathEvent.BUS.addListener((LivingDeathEvent event) -> {
            GeodeGraceEffect.onEntityDeath(event.getEntity(), event.getSource().getEntity());
            SoulMateEffect.onEntityDeath(event.getEntity());
        });
    }

    // ----- EntityListeners.createDefaultAttributes + NeoAttributeEvents -----

    private static void registerAttributes() {
        EntityAttributeCreationEvent.BUS.addListener((EntityAttributeCreationEvent event) ->
                event.put(Entities.GRUNGLER.value(), Grungler.createAttributes().build()));

        EntityAttributeModificationEvent.BUS.addListener((EntityAttributeModificationEvent event) ->
                Attributes.getAllAttributes().forEach(attributeHolder -> event.add(EntityType.PLAYER, attributeHolder)));
    }

    // ----- NeoCommandEvents -----

    private static void registerCommands() {
        RegisterCommandsEvent.BUS.addListener((RegisterCommandsEvent event) -> PpCommands.register(event.getDispatcher()));
    }

    // ----- NeoDelayedEvents + NeoServerTickEvents + PlayerListeners.onTick -----

    private static void registerTicks() {
        TickEvent.ServerTickEvent.Post.BUS.addListener((TickEvent.ServerTickEvent.Post event) -> {
            DelayedEvents.tick(TickHandler.ticks());
            ServerTickHandler.increment();
        });

        TickEvent.ServerTickEvent.Pre.BUS.addListener((TickEvent.ServerTickEvent.Pre event) ->
                applyAllPassiveItemPotionEffects(event.server().getPlayerList().getPlayers()));
    }

    // ----- PlayerListeners + EntityListeners.onBreakBlock + onEntityDeath -----

    private static void registerPlayerListeners() {
        // ItemEntityPickupEvent fires before the pickup; the ItemEntity's stack is still whole.
        EntityItemPickupEvent.BUS.addListener((EntityItemPickupEvent event) -> onItemPickedUp(event));

        // Grungler.onEntityDeath (no Forge BlockDropsEvent, so Grungler spawns via BreakEvent below).
        LivingDeathEvent.BUS.addListener((LivingDeathEvent event) -> Grungler.onEntityDeath(event.getEntity()));

        // RightClickBlock is cancellable via the predicate's boolean return.
        PlayerInteractEvent.RightClickBlock.BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
            var pos = event.getPos();
            boolean mossHandled = MossBehaviour.doMossInteractions(event.getLevel(), pos, event.getItemStack(), event.getEntity(), event.getHand());
            boolean clotheslineHandled = ClotheslineBehaviour.doClotheslineInteractions(event.getLevel(), pos, event.getItemStack(), event.getEntity(), event.getHand());
            return mossHandled || clotheslineHandled;
        });

        EntityJoinLevelEvent.BUS.addListener((EntityJoinLevelEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                PacketNetwork.sendToPlayers(player,
                        new ClientboundSyncKnownBrewingRecipesPacket(SavedData.instance.getData(player).getKnownRecipeKeys()),
                        new CustomPacketPayload[]{new ClientboundSyncPairedAbyssalTrove(SavedData.instance.getData(player).getPairedAbyssalTrovePos())}
                );
            }
        });

        // BlockEvent.BreakEvent is HasResult-based (no BlockDropsEvent on Forge); reconstruct drops.
        BlockEvent.BreakEvent.BUS.addListener((BlockEvent.BreakEvent event) -> {
            if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
                return;
            }
            List<ItemStack> drops = Block.getDrops(event.getState(), serverLevel, event.getPos(), serverLevel.getBlockEntity(event.getPos()));
            if (Grungler.onBreakBlock(event.getState(), drops, event.getPlayer(), event.getPos())) {
                event.setResult(Result.DENY);
            }
        });
    }

    private static void onItemPickedUp(EntityItemPickupEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.computeIfAbsent(serverPlayer.getUUID(), (uuid) -> new PlayerBrewingKnowledge());
        ItemStack stack = event.getItem().getItem().copy();
        stack.setCount(1);
        PpIngredient ppIngredient = PpIngredient.of(stack);

        // Create a priority queue to sort the alerts by priority - an item could trigger multiple alerts, so only send the highest priority alert
        PriorityQueue<Pair<ClientboundDisplayAlertWithItemStackName, Integer>> alerts = new PriorityQueue<>((a, b) -> Integer.compare(a.getSecond(), b.getSecond()));
        List<RecipeHolder<BrewingCauldronRecipe>> learnedRecipes = new ArrayList<>();

        // Get all *recipe* knowledge that is triggered by picking up this ingredient. If there is any, try to trigger an alert for the respective category.
        int count;
        learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(Recipes.DURATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
        if (!learnedRecipes.isEmpty()) {
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.duration_ingredient", stack, true), 1));
        }
        count = learnedRecipes.size();
        learnedRecipes.addAll(PlayerBrewingKnowledge.getUnknownRecipesWithIngredient(Recipes.AMPLIFICATION_UPGRADE_ANALYSIS, ppIngredient, playerBrewingKnowledge));
        if (learnedRecipes.size() > count) {
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.amplification_ingredient", stack, true), 2));
        }

        // Add the *ingredient* to the player's knowledge if it is unknown
        stack.setCount(1);
        if (playerBrewingKnowledge.isIngredientUnknown(stack) && AbyssalTroveBlockEntity.getAcceptedIngredients().contains(ppIngredient)) {
            playerBrewingKnowledge.addIngredient(stack);
            alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.acquired_ingredient_knowledge_" + serverPlayer.getRandom().nextInt(1, 4), stack, true), 3));
        }

        // Gather all the packets to send to player
        List<CustomPacketPayload> packets = new ArrayList<>();
        if (!learnedRecipes.isEmpty()) {
            for (RecipeHolder<BrewingCauldronRecipe> recipe : learnedRecipes) {
                playerBrewingKnowledge.addKnownRecipe(recipe.id());
            }
            packets.add(new ClientboundSyncKnownBrewingRecipesPacket(learnedRecipes.stream().map(RecipeHolder::id).toList()));
        }
        if (!alerts.isEmpty()) {
            packets.add(alerts.poll().getFirst());
        }

        if (!packets.isEmpty()) {
            CustomPacketPayload first = packets.getFirst();
            CustomPacketPayload[] rest = packets.stream().skip(1).toArray(CustomPacketPayload[]::new);
            PacketNetwork.sendToPlayers(serverPlayer, first, rest);
        }
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

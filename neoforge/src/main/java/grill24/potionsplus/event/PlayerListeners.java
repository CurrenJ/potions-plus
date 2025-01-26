package grill24.potionsplus.event;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import grill24.potionsplus.block.UraniumOreBlock;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.network.ClientboundSyncPairedAbyssalTrove;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.AttributeModifiersWhileHeldAbility;
import grill24.potionsplus.utility.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import oshi.util.tuples.Pair;

import java.util.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerListeners {

    @SubscribeEvent
    public static void onItemPickedUp(final ItemEntityPickupEvent.Post event) {
        Level level = event.getPlayer().level();
        if (!level.isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.computeIfAbsent(player.getUUID(), (uuid) -> new PlayerBrewingKnowledge());
            ItemStack stack = event.getOriginalStack().copy();
            stack.setCount(1);
            PpIngredient ppIngredient = PpIngredient.of(stack);

            // Create a priority queue to sort the alerts by priority - an item could trigger multiple alerts, so only send the highest priority alert
            PriorityQueue<Pair<ClientboundDisplayAlertWithItemStackName, Integer>> alerts = new PriorityQueue<>((a, b) -> Integer.compare(a.getB(), b.getB()));
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
            // For some reason when I made the saved data I decided to use itemstacks, but we only really care about the item id. So set count to 1 for consistency.
            stack.setCount(1);
            if (playerBrewingKnowledge.isIngredientUnknown(stack) && AbyssalTroveBlockEntity.getAcceptedIngredients().contains(ppIngredient)) {
                // At the time of writing this, *ingredient* knowledge is not synced to the client, because it is only used for server-side checks. If this changes, we should sync it here.
                playerBrewingKnowledge.addIngredient(stack);
                // Alert the player that they have picked up this brewing ingredient for the first time.
                alerts.add(new Pair<>(new ClientboundDisplayAlertWithItemStackName("chat.potionsplus.acquired_ingredient_knowledge_" + player.getRandom().nextInt(1, 4), stack, true), 3));
            }


            // Gather all the packets to send to player
            List<CustomPacketPayload> packets = new ArrayList<>();
            if (!learnedRecipes.isEmpty()) {
                // Update the server-side recipe knowledge
                for (RecipeHolder<BrewingCauldronRecipe> recipe : learnedRecipes) {
                    playerBrewingKnowledge.addKnownRecipe(recipe.id().toString());
                }
                // Sync new recipe knowledge to the client
                packets.add(ClientboundSyncKnownBrewingRecipesPacket.of(learnedRecipes.stream().map(RecipeHolder::id).map(Object::toString).toList()));
            }
            if (!alerts.isEmpty()) {
                // Only send the highest priority alert
                packets.add(alerts.poll().getA());
            }

            // Send the packets
            if (!packets.isEmpty()) {
                CustomPacketPayload first = packets.getFirst();
                CustomPacketPayload[] rest = packets.stream().skip(1).toArray(CustomPacketPayload[]::new);
                PacketDistributor.sendToPlayer(player, first, rest);
            }
        }
    }

    private static final int EFFECT_DURATION = 20 * 15; // Every 15 seconds
    private static int lastEffectActivation;

    @SubscribeEvent
    public static void onTick(final ServerTickEvent.Pre event) {
        applyAllPassiveItemPotionEffects(event.getServer().getPlayerList().getPlayers());

        AttributeModifiersWhileHeldAbility.onTick(event);
        SkillsData.tickPointEarningHistory(event);
    }

    private static void applyAllPassiveItemPotionEffects(List<ServerPlayer> players) {
        // Apply passive item potion effects every interval of time
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
        if (PUtil.isPassivePotionEffectItem(stack)) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            if (potionContents != null) {
                List<MobEffectInstance> customEffects = new ArrayList<>();
                for (MobEffectInstance effect : PUtil.getAllEffects(potionContents)) {
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
                stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potionContents.potion(), potionContents.customColor(), customEffects));
            }
        }
    }

    @SubscribeEvent
    public static void on(final PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();

        MossBehaviour.doMossInteractions(event, pos);
        ClotheslineBehaviour.doClotheslineInteractions(event);
    }

    @SubscribeEvent
    public static void onPlayerJoin(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player,
                    ClientboundSyncKnownBrewingRecipesPacket.of(SavedData.instance.getData(player).getKnownRecipesSerializableData()),
                    new ClientboundSyncPairedAbyssalTrove(SavedData.instance.getData(player).getPairedAbyssalTrovePos()),
                    new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player))
//                    new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player))
            );
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        UraniumOreBlock.tryLeftClickBlock(event);
    }
}

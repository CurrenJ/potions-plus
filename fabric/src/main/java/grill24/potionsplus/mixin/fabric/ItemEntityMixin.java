package grill24.potionsplus.mixin.fabric;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundDisplayAlertWithItemStackName;
import grill24.potionsplus.network.ClientboundSyncKnownBrewingRecipesPacket;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.platform.PacketNetwork;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Fabric equivalent of NeoForge's {@code PlayerListeners.onItemPickedUp}
 * ({@code ItemEntityPickupEvent.Post}). Fabric has no such event, so we mixin into
 * {@link ItemEntity#playerTouch(Player)} at the point where the pickup actually succeeds.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private void potionsplus$onItemPickedUp(Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PlayerBrewingKnowledge playerBrewingKnowledge = SavedData.instance.playerDataMap.computeIfAbsent(serverPlayer.getUUID(), (uuid) -> new PlayerBrewingKnowledge());
        ItemStack stack = this.getItem().copy();
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
}

package grill24.potionsplus.event;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.network.ClientboundStartFishingMinigamePacket;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FishingListeners {
    @SubscribeEvent
    public static void onItemFished(final ItemFishedEvent event) {
        Player player = event.getEntity();
        ItemStack rod = player.getItemInHand(player.getUsedItemHand());
        if (player instanceof ServerPlayer serverPlayer && rod.getItem() instanceof FishingRodItem) {
            LootParams lootParams = createLootParams(serverPlayer, event.getHookEntity(), rod);
            LootTable lootTable = serverPlayer.level().getServer().reloadableRegistries().getLootTable(LootTables.FISHING);
            List<ItemStack> loot = lootTable.getRandomItems(lootParams);

            if (!loot.isEmpty()) {
                ItemStack reward = loot.getFirst();

                event.getDrops().clear();
                event.getDrops().add(reward);

                PacketDistributor.sendToPlayer(
                        serverPlayer,
                        ClientboundStartFishingMinigamePacket.create(
                                serverPlayer,
                                new FishingGamePlayerAttachment(reward, getFrame(player.registryAccess(), reward))
                        )
                );
                rod.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                event.setCanceled(true);
            }
        }
    }

    private static ItemStack getFrame(RegistryAccess registryAccess, ItemStack reward) {
        Iterable<Holder<Item>> copper = registryAccess.registryOrThrow(Registries.ITEM).getTagOrEmpty(Tags.Items.PP_FISHING_COPPER_FRAME);
        Iterable<Holder<Item>> gold = registryAccess.registryOrThrow(Registries.ITEM).getTagOrEmpty(Tags.Items.PP_FISHING_GOLD_FRAME);
        Iterable<Holder<Item>> diamond = registryAccess.registryOrThrow(Registries.ITEM).getTagOrEmpty(Tags.Items.PP_FISHING_DIAMOND_FRAME);
        Iterable<Holder<Item>> purple = registryAccess.registryOrThrow(Registries.ITEM).getTagOrEmpty(Tags.Items.PP_FISHING_PURPLE_FRAME);

        if (reward.is(Tags.Items.PP_FISHING_COPPER_FRAME)) {
            return Items.GENERIC_ICON.getItemStackForTexture(Items.COPPER_FISHING_FRAME_TEX_LOC);
        } else if (reward.is(Tags.Items.PP_FISHING_GOLD_FRAME)) {
            return Items.GENERIC_ICON.getItemStackForTexture(Items.GOLD_FISHING_FRAME_TEX_LOC);
        } else if (reward.is(Tags.Items.PP_FISHING_DIAMOND_FRAME)) {
            return Items.GENERIC_ICON.getItemStackForTexture(Items.DIAMOND_FISHING_FRAME_TEX_LOC);
        } else if (reward.is(Tags.Items.PP_FISHING_PURPLE_FRAME)) {
            return Items.GENERIC_ICON.getItemStackForTexture(Items.PURPLE_FISHING_FRAME_TEX_LOC);
        }

        return ItemStack.EMPTY;
    }

    private static LootParams createLootParams(ServerPlayer player, FishingHook hook, ItemStack rod) {
        return new LootParams.Builder((ServerLevel) player.level())
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.TOOL, rod)
                .withParameter(LootContextParams.THIS_ENTITY, hook)
                .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                .withLuck((float) 0 + player.getLuck())
                .create(LootContextParamSets.FISHING);
    }
}

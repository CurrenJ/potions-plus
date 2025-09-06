package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.network.ClientboundDisplayTossupAnimationPacket;
import grill24.potionsplus.utility.HolderCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ConfiguredGrantableReward<RC extends GrantableRewardConfiguration, R extends GrantableReward<RC>> (
        R reward, RC config) {
    public static final Codec<ConfiguredGrantableReward<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.GRANTABLE_REWARD
            .byNameCodec()
            .dispatch(configured -> configured.reward, GrantableReward::configuredCodec);
    public static final Codec<Holder<ConfiguredGrantableReward<?, ?>>> CODEC = RegistryFileCodec.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, DIRECT_CODEC);

    public static final HolderCodecs<ConfiguredGrantableReward<?, ?>> HOLDER_CODECS = new HolderCodecs<>(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, DIRECT_CODEC);

    @Override
    public String toString() {
        return "Reward: " + this.reward + ": " + this.config;
    }

    public void grant(ResourceKey<ConfiguredGrantableReward<?, ?>> holder, ServerPlayer player) {
        List<ItemStack> itemsBefore = player.getInventory().getNonEquipmentItems().stream().map(ItemStack::copy).toList();

        this.reward.grant(holder, this.config, player);

        List<ItemStack> itemsAfter = player.getInventory().getNonEquipmentItems();

        // Get difference between itemsBefore and itemsAfter
        List<ItemStack> newItems = new ArrayList<>();
        for (int i = 0; i < itemsBefore.size(); i++) {
            ItemStack before = itemsBefore.get(i);
            ItemStack after = itemsAfter.get(i);
            if (!ItemStack.isSameItemSameComponents(before, after) || before.getCount() != after.getCount()) {
                newItems.add(after);
            }
        }

        // Display the item activation
        if (!newItems.isEmpty()) {
            PacketDistributor.sendToPlayer(player, new ClientboundDisplayTossupAnimationPacket(newItems, 5, 0.75F));
        }
    }

    public Optional<Component> getDescription(RegistryAccess registryAccess) {
        return this.reward.getDescription(registryAccess, config);
    }

    public List<List<Component>> getMultiLineRichDescription(RegistryAccess registryAccess) {
        return this.reward.getMultiLineRichDescription(registryAccess, config);
    }
}

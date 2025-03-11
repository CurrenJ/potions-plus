package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Represents the owner of a given item. Used for tracking who owns an ItemStack.
 * Used with {@link EdibleRewardGranterDataComponent} to ensure that the player
 * who eats the item is the one who was earned the reward.
 * @param uuid
 * @param playerDisplayName
 */
public record OwnerDataComponent(UUID uuid, String playerDisplayName) {
    public static final Codec<OwnerDataComponent> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                    Codec.STRING.fieldOf("uuid").xmap(UUID::fromString, UUID::toString).forGetter(OwnerDataComponent::uuid),
                    Codec.STRING.fieldOf("playerDisplayName").forGetter(OwnerDataComponent::playerDisplayName)
            ).apply(codecBuilder, OwnerDataComponent::new));
    public static final StreamCodec<ByteBuf, OwnerDataComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public boolean isOwner(UUID uuid) {
        return this.uuid.equals(uuid);
    }

    public boolean isOwner(Player player) {
        return isOwner(player.getUUID());
    }

    public static OwnerDataComponent fromPlayer(Player player) {
        return new OwnerDataComponent(player.getUUID(), player.getDisplayName().getString());
    }

    /**
     * Returns a tooltip component that displays the owner of the item.
     * @return the tooltip component
     */
    public MutableComponent getTooltipComponent() {
        return Component.translatable(Translations.TOOLTIP_POTIONSPLUS_REWARD_OWNER, playerDisplayName).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    }
}

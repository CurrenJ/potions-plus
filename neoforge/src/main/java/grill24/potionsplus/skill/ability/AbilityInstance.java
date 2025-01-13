package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.neoforged.neoforge.network.PacketDistributor;

public class AbilityInstance {
    public static final Codec<AbilityInstance> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("ability").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled)
    ).apply(codecBuilder, AbilityInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityInstance> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            AbilityInstance::new);

    protected final Holder<ConfiguredPlayerAbility<?, ?>> ability;
    protected boolean isEnabled;

    // For deserialization
    private AbilityInstance(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this.ability = ability;
        this.isEnabled = isEnabled;
    }

    public AbilityInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this.ability = ability;

        if (isEnabled && getConfiguredAbility().config().getData().enabledByDefault()) {
            tryEnable(player);
        } else {
            tryDisable(player);
        }
    }

    public  <E, AC extends PlayerAbilityConfiguration, A extends PlayerAbility<E, AC>> ConfiguredPlayerAbility<AC, A> getConfiguredAbility() {
        return (ConfiguredPlayerAbility<AC, A>) ability.value();
    }

    public Holder<ConfiguredPlayerAbility<?, ?>> getHolder() {
        return this.ability;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean tryEnable(ServerPlayer player) {
        this.isEnabled = true;
        getConfiguredAbility().onAbilityGranted(player);
        return true;
    }

    public void tryDisable(ServerPlayer player) {
        if (!this.isEnabled) {
            return;
        }
        this.isEnabled = false;
        getConfiguredAbility().onAbilityRevoked(player);
    }

    public boolean toggle(ServerPlayer player) {
        boolean enabled = false;
        if (this.isEnabled) {
            tryDisable(player);
        } else {
            tryEnable(player);
            enabled = true;
        }

        // Send updated skill data to the client
        PacketDistributor.sendToPlayer(player, new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player)));

        return enabled;
    }

    public void toggle(LocalPlayer player) {
        String s = StringUtil.filterText(getToggleCommand());
        if (s.startsWith("/")) {
            if (!player.connection.sendUnsignedCommand(s.substring(1))) {
                PotionsPlus.LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", s);
            }
        } else {
            PotionsPlus.LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", s);
        }
    }

    public Component getDescription() {
        return getDescription(false);
    }

    public Component getDescription(boolean showEnablementText) {
        MutableComponent component = Component.empty();

        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getToggleCommand());
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Translations.GENERIC_POTIONSPLUS_TOGGLE));
        Style style = Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent).withColor(this.isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED);

        if (showEnablementText) {
            component.append(Component.literal("[").withStyle(style));
            component.append(Component.translatable(this.isEnabled ? Translations.GENERIC_POTIONSPLUS_ENABLED : Translations.GENERIC_POTIONSPLUS_DISABLED).withStyle(style));
            component.append(Component.literal("] ").withStyle(style));
        }
        component.append(getConfiguredAbility().getDescription().copy().withStyle(style));


        return component;
    }

    private String getToggleCommand() {
        return "/potionsplus skill ability byId " + ability.getKey().location() + " toggle";
    }
}

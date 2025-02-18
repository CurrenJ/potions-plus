package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ServerboundUpdateAbilityStrengthPacket;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.IAdjustableStrengthAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.PacketDistributor;

public class AdjustableStrengthAbilityInstanceData extends SimpleAbilityInstanceData
{
    protected float abilityStrength;
    protected float maxAbilityStrength;

    public static final Codec<AdjustableStrengthAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength)
    ).apply(codecBuilder, AdjustableStrengthAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdjustableStrengthAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            AdjustableStrengthAbilityInstanceData::new);

    // For deserialization
    public AdjustableStrengthAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float maxAbilityStrength) {
        super(ability, isEnabled);

        this.abilityStrength = abilityStrength;
        this.maxAbilityStrength = maxAbilityStrength;
    }

    public AdjustableStrengthAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this(ability, isEnabled, 0F, 0F);
    }

    public void setAbilityStrength(float strength) {
        this.abilityStrength = Math.clamp(strength, 0, maxAbilityStrength);
    }

    public float getAbilityStrength() {
        return this.abilityStrength;
    }

    public void setMaxAbilityStrength(float maxStrength) {
        this.maxAbilityStrength = maxStrength;
    }

    public void increaseMaxAbilityStrength(float increase) {
        this.maxAbilityStrength += increase;
    }

    public float getMaxAbilityStrength() {
        return this.maxAbilityStrength;
    }

    @Override
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

        Component description = getConfiguredAbility().getDescription();
        if (getConfiguredAbility().ability() instanceof IAdjustableStrengthAbility adjustableStrengthSkill) {
            description = adjustableStrengthSkill.getDescription(getConfiguredAbility().config(), abilityStrength);
        }
        component.append(description.copy().withStyle(style));


        return component;
    }

    public void clientRequestDecreaseStrength(LocalPlayer player) {
        PacketDistributor.sendToServer(new ServerboundUpdateAbilityStrengthPacket(this.ability.getKey().location(), -.25F, ServerboundUpdateAbilityStrengthPacket.Operation.ADD));
    }

    public void clientRequestIncreaseStrength(LocalPlayer player) {
        PacketDistributor.sendToServer(new ServerboundUpdateAbilityStrengthPacket(this.ability.getKey().location(), .25F, ServerboundUpdateAbilityStrengthPacket.Operation.ADD));
    }
}

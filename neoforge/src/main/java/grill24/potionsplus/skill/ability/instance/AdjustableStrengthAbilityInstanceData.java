package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ServerboundUpdateAbilityStrengthPacket;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class AdjustableStrengthAbilityInstanceData extends SimpleAbilityInstanceData
{
    protected float abilityStrength;
    protected float minAbilityStrength;
    protected float maxAbilityStrength;
    protected float stepSize;

    public static final Codec<AdjustableStrengthAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled),
            Codec.FLOAT.optionalFieldOf("abilityStrength", 1F).forGetter(instance -> instance.abilityStrength),
            Codec.FLOAT.optionalFieldOf("minAbilityStrength", 0F).forGetter(instance -> instance.minAbilityStrength),
            Codec.FLOAT.optionalFieldOf("maxAbilityStrength", 1F).forGetter(instance -> instance.maxAbilityStrength),
            Codec.FLOAT.optionalFieldOf("stepSize", 1F).forGetter(instance -> instance.stepSize)
    ).apply(codecBuilder, AdjustableStrengthAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdjustableStrengthAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.abilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.minAbilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.maxAbilityStrength,
            ByteBufCodecs.FLOAT,
            (instance) -> instance.stepSize,
            AdjustableStrengthAbilityInstanceData::new);

    // For deserialization
    public AdjustableStrengthAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled, float abilityStrength, float minAbilityStrength, float maxAbilityStrength, float stepSize) {
        super(ability, isEnabled);

        this.abilityStrength = abilityStrength;
        this.minAbilityStrength = minAbilityStrength;
        this.maxAbilityStrength = maxAbilityStrength;
        this.stepSize = stepSize;
    }

    public AdjustableStrengthAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this(ability, isEnabled, 0F, 0F, 0F, 1F);
    }

    public void setAbilityStrength(float strength) {
        this.abilityStrength = Math.clamp(strength, minAbilityStrength, maxAbilityStrength);
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

        ClickEvent clickEvent = new ClickEvent.RunCommand(getToggleCommand());
        HoverEvent hoverEvent = new HoverEvent.ShowText(Component.translatable(Translations.GENERIC_POTIONSPLUS_TOGGLE));
        Style style = Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent).withColor(this.isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED);

        if (showEnablementText) {
            component.append(Component.literal("[").withStyle(style));
            component.append(Component.translatable(this.isEnabled ? Translations.GENERIC_POTIONSPLUS_ENABLED : Translations.GENERIC_POTIONSPLUS_DISABLED).withStyle(style));
            component.append(Component.literal("] ").withStyle(style));
        }

        // Get string of ability strength with decimal places
        String strength = Utility.autoFormatNumber(abilityStrength);
        Component description = getConfiguredAbility().getDescription(strength);
        component.append(description.copy().withStyle(style));


        return component;
    }

    public void clientRequestDecreaseStrength(LocalPlayer player) {
        PacketDistributor.sendToServer(new ServerboundUpdateAbilityStrengthPacket(this.ability.getKey().location(), -stepSize, ServerboundUpdateAbilityStrengthPacket.Operation.ADD));
    }

    public void clientRequestIncreaseStrength(LocalPlayer player) {
        PacketDistributor.sendToServer(new ServerboundUpdateAbilityStrengthPacket(this.ability.getKey().location(), stepSize, ServerboundUpdateAbilityStrengthPacket.Operation.ADD));
    }
}

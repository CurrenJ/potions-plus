package grill24.potionsplus.skill.ability.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbilityConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public class SimpleAbilityInstanceData {
    public static final Codec<SimpleAbilityInstanceData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("type").forGetter(instance -> instance.ability),
            Codec.BOOL.optionalFieldOf("isEnabled", false).forGetter(instance -> instance.isEnabled)
    ).apply(codecBuilder, SimpleAbilityInstanceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleAbilityInstanceData> STREAM_CODEC = StreamCodec.composite(
            ConfiguredPlayerAbility.HOLDER_CODECS.holderStreamCodec(),
            (instance) -> instance.ability,
            ByteBufCodecs.BOOL,
            (instance) -> instance.isEnabled,
            SimpleAbilityInstanceData::new);

    protected final Holder<ConfiguredPlayerAbility<?, ?>> ability;
    protected boolean isEnabled;

    public SimpleAbilityInstanceData(Holder<ConfiguredPlayerAbility<?, ?>> ability, boolean isEnabled) {
        this.ability = ability;
        this.isEnabled = isEnabled;
    }

    public  <E, AC extends PlayerAbilityConfiguration, A extends PlayerAbility<AC>> ConfiguredPlayerAbility<AC, A> getConfiguredAbility() {
        return (ConfiguredPlayerAbility<AC, A>) ability.value();
    }

    public Holder<ConfiguredPlayerAbility<?, ?>> getHolder() {
        return this.ability;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public Component getDescription() {
        return getDescription(false);
    }

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

        Component description = getConfiguredAbility().getDescription();
        component.append(description.copy().withStyle(style));


        return component;
    }

    protected String getToggleCommand() {
        return "/potionsplus skill ability byId " + ability.getKey().location() + " toggle";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleAbilityInstanceData that = (SimpleAbilityInstanceData) o;

        if (isEnabled != that.isEnabled) return false;
        return ability.getKey() != null && ability.getKey().equals(that.ability.getKey());
    }
}

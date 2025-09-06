package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.SimpleTooltipScreenElement;
import grill24.potionsplus.gui.TextComponentScreenElement;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class AbilityTextScreenElement extends HorizontalListScreenElement<RenderableScreenElement> {
    private ResourceLocation abilityId;

    private static final Color ENABLED_COLOR = new Color(0x00FF00);
    private static final Color DISABLED_COLOR = new Color(0xFF0000);

    private TextButtonScreenElement buttonDecrease;
    private TextComponentScreenElement text;
    private TextButtonScreenElement buttonIncrease;

    private Component abilityTextComponent;

    public AbilityTextScreenElement(Screen screen, ResourceLocation abilityId, List<Component> components) {
        this(screen, abilityId, compactComponents(components));
    }

    public AbilityTextScreenElement(Screen screen, ResourceLocation abilityId, Component abilityTextComponent) {
        super(screen, Settings.DEFAULT, YAlignment.CENTER, 5);

        this.abilityId = abilityId;
        this.abilityTextComponent = abilityTextComponent;
        initializeChildren(abilityTextComponent);
    }

    private void initializeChildren(Component component) {
        Optional<AbilityInstanceSerializable<?, ?>> ability = getAbility();
        if (ability.isEmpty()) {
            return;
        }

        text = new TextComponentScreenElement(screen, Settings.DEFAULT, getTargetColor(ability.get().data().isEnabled()), component);
        text.addClickListener((mouseX, mouseY, button, element) -> ability.get().toggleClient());
        float buttonSize = (float) text.getGlobalBounds().getHeight();

        buttonDecrease = new TextButtonScreenElement(screen, this, Settings.DEFAULT, Component.literal("-"), buttonSize, buttonSize);
        buttonDecrease.addClickListener((mouseX, mouseY, button, element) -> {
            if (ability.get().data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrength) {
                adjustableStrength.clientRequestDecreaseStrength(this.screen.getMinecraft().player);
            }
        });
        buttonIncrease = new TextButtonScreenElement(screen, this, Settings.DEFAULT, Component.literal("+"), buttonSize, buttonSize);
        buttonIncrease.addClickListener((mouseX, mouseY, button, element) -> {
            if (ability.get().data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrength) {
                adjustableStrength.clientRequestIncreaseStrength(this.screen.getMinecraft().player);
            }
        });

        if (!(ability.get().data() instanceof AdjustableStrengthAbilityInstanceData)) {
            buttonDecrease.hide(false);
            buttonIncrease.hide(false);
        }

        setChildren(List.of(buttonDecrease, text, buttonIncrease));

        this.tooltip = getSimpleTooltipScreenElement();
    }

    private @NotNull SimpleTooltipScreenElement getSimpleTooltipScreenElement() {
        SimpleTooltipScreenElement tooltip = new SimpleTooltipScreenElement(this.screen, Settings.DEFAULT.withAnchor(Anchor.BOTTOM_LEFT).withAnimationSpeed(1.0F).withHiddenByDefault(true), Color.WHITE, () -> {
            Optional<AbilityInstanceSerializable<?, ?>> abilityInstance = getAbility();
            Optional<List<List<Component>>> text = abilityInstance.flatMap(AbilityInstanceSerializable::getLongDescription);
            return text.orElseGet(List::of);
        });
        tooltip.setPadding(new Vector4f(2, 1, 2, 1));
        return tooltip;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        Optional<AbilityInstanceSerializable<?, ?>> ability = getAbility();
        if (ability.isEmpty()) {
            return;
        }

        text.setTargetColor(getTargetColor(ability.get().data().isEnabled()));
        text.setComponent(ability.get().data().getDescription(), false);
        if (this.tooltip instanceof SimpleTooltipScreenElement t) {
            t.update();
        }
        super.onTick(partialTick, mouseX, mouseY);
    }

    private static Component compactComponents(List<Component> components) {
        MutableComponent component = Component.empty();
        for (Component c : components) {
            component.append(c);
        }
        return component;
    }

    private Color getTargetColor(boolean enabled) {
        return enabled ? ENABLED_COLOR : DISABLED_COLOR;
    }

    public void setAbility(ResourceLocation abilityId) {
        this.abilityId = abilityId;
    }

    public Optional<AbilityInstanceSerializable<?, ?>> getAbility() {
        Player player = this.screen.getMinecraft().player;
        if (player == null) {
            return Optional.empty();
        }
        return SkillsData.getPlayerData(player).getAbilityInstance(player.registryAccess(), abilityId);
    }

    public ResourceLocation getAbilityId() {
        return abilityId;
    }

    @Override
    protected void onMouseEnter(int x, int y) {
        if (this.tooltip != null) {
            if (this.tooltip instanceof SimpleTooltipScreenElement t) {
                t.update();
            }
            this.tooltip.show();
        }
    }

    @Override
    protected void onMouseExit(int x, int y) {
        if (this.tooltip != null) {
            this.tooltip.hide(false);
        }
    }
}

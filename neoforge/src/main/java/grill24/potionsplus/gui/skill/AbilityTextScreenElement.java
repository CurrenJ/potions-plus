package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.TextComponentScreenElement;
import grill24.potionsplus.skill.ability.AbilityInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class AbilityTextScreenElement extends TextComponentScreenElement {
    private AbilityInstance ability;

    private static final Color ENABLED_COLOR = new Color(0x00FF00);
    private static final Color DISABLED_COLOR = new Color(0xFF0000);

    public AbilityTextScreenElement(Screen screen, AbilityInstance ability, List<Component> components) {
        this(screen, ability, compactComponents(components));
    }

    public AbilityTextScreenElement(Screen screen, AbilityInstance ability, Component component) {
        super(screen, Settings.DEFAULT, ENABLED_COLOR, component);

        this.ability = ability;
    }

    @Override
    protected void onTick(float partialTick) {
        setTargetColor(getTargetColor(ability.isEnabled()));

        super.onTick(partialTick);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        Rectangle2D bounds = getGlobalBounds();
        if (bounds.contains(mouseX, mouseY)) {
            ability.toggle(this.screen.getMinecraft().player);
        }

        super.click(mouseX, mouseY);
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

    public void setAbility(AbilityInstance ability) {
        this.ability = ability;
    }

    public AbilityInstance getAbility() {
        return ability;
    }
}

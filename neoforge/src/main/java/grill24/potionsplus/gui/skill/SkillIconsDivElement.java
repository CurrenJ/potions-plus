package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.DivScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.gui.screens.Screen;

public class SkillIconsDivElement extends DivScreenElement<SkillIconsScreenElement> {
    public SkillIconsDivElement(Screen screen, SkillIconsScreenElement child) {
        super(screen, null, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.CENTER, child);

        setAllowClicksOutsideBounds(true);
    }

    @Override
    protected float getWidth() {
        if (getChildren().isEmpty()) {
            return 0;
        }

        if (getChildren().iterator().next() instanceof SkillIconsScreenElement skillsIcons) {
            return skillsIcons.getRadius() * 2;
        }
        return 0;
    }

    @Override
    protected float getHeight() {
        if (getChildren().isEmpty()) {
            return 0;
        }

        if (getChildren().iterator().next() instanceof SkillIconsScreenElement skillsIcons) {
            return skillsIcons.getRadius() * 2;
        }
        return 0;
    }
}

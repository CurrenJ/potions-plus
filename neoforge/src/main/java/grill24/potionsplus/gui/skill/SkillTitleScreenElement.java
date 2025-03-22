package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.TextComponentScreenElement;
import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillInstance;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector4f;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SkillTitleScreenElement extends VerticalListScreenElement<RenderableScreenElement> {
    private final TextComponentScreenElement title;
    private final TextComponentScreenElement level;
    private final ProgressBarElement progressBar;

    public SkillTitleScreenElement(Screen screen, ResourceKey<ConfiguredSkill<?, ?>> skill) {
        super(screen, Settings.DEFAULT, XAlignment.CENTER);

        this.title = new TextComponentScreenElement(
                this.screen,
                Settings.DEFAULT.withPadding(new Vector4f(0, 50, 0, 0)),
                Color.WHITE,
                Component.empty()
        );
        this.level = new TextComponentScreenElement(
                this.screen,
                Settings.DEFAULT,
                Color.WHITE,
                Component.empty()
        );
        this.progressBar = new ProgressBarElement(
                this.screen,
                Settings.DEFAULT,
                0
        );
        this.setChildren(List.of(this.title, this.level, this.progressBar));

        setSelectedSkill(skill);
    }

    public SkillTitleScreenElement(Screen screen) {
        this(screen, null);
    }

    public void setBlank() {
        this.title.setComponent(Component.empty(), false);
        this.level.setComponent(Component.empty(), false);
        this.progressBar.setTargetWidth(0);
    }

    public void setSelectedSkill(ResourceKey<ConfiguredSkill<?, ?>> skill) {
        if (skill == null || this.screen.getMinecraft().player == null) {
            setBlank();
            return;
        }

        Player player = this.screen.getMinecraft().player;
        RegistryAccess registryAccess = player.registryAccess();
        Optional<SkillInstance<?, ?>> configuredSkill = SkillsData.getPlayerData(this.screen.getMinecraft().player).getOrCreate(registryAccess, skill);
        if (configuredSkill.isEmpty()) {
            setBlank();
            return;
        }

        float partialLevel = configuredSkill.get().getPartialLevel(registryAccess);
        float progressToNextLevel = partialLevel % 1;
        int level = (int) partialLevel;

        Component skillName = Component.translatable(configuredSkill.get().getConfiguredSkill(registryAccess).config().getData().translationKey());
        this.title.setComponent(skillName, true);

        Component skillLevel = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_LEVEL, level);
        this.level.setComponent(skillLevel, true);

        this.progressBar.setProgress(progressToNextLevel);
        this.progressBar.setTargetWidth(this.progressBar.getDefaultWidth());
    }
}

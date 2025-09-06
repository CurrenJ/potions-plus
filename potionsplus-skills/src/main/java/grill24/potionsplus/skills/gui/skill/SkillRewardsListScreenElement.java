package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.TextComponentScreenElement;
import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillInstance;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillRewardsListScreenElement extends VerticalListScreenElement<RenderableScreenElement> {
    private ResourceKey<ConfiguredSkill<?, ?>> skillKey;

    public SkillRewardsListScreenElement(Screen screen, Settings settings, XAlignment alignment, RenderableScreenElement... elements) {
        super(screen, settings, alignment, 3, elements);
    }

    public SkillRewardsListScreenElement(Screen screen, Settings settings, XAlignment alignment, float paddingBetweenElements, RenderableScreenElement... elements) {
        super(screen, settings, alignment, paddingBetweenElements, elements);
    }

    public void setSelectedSkill(ResourceKey<ConfiguredSkill<?, ?>> key) {
        this.skillKey = key;
        this.setChildren(createRewardDisplays());
    }

    public List<RenderableScreenElement> createRewardDisplays() {
        // Nothing to display when no skill is selected
        if (this.skillKey == null || this.screen.getMinecraft().player == null) {
            return List.of();
        }

        RegistryAccess registryAccess = this.screen.getMinecraft().player.registryAccess();
        Optional<Holder.Reference<ConfiguredSkill<?, ?>>> configuredSkillRef = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).get(skillKey);
        if (configuredSkillRef.isEmpty()) {
            return List.of();
        }

        ConfiguredSkill<?, ?> configuredSkill = configuredSkillRef.get().value();
        SkillLevelUpRewardsConfiguration rewardsConfiguration = configuredSkill.config().getData().rewardsConfiguration();
        SkillsData skillsData = SkillsData.getPlayerData(this.screen.getMinecraft().player);
        Optional<SkillInstance<?, ?>> skillInstance = skillsData.getOrCreate(registryAccess, skillKey);
        if (skillInstance.isEmpty()) {
            return List.of();
        }
        int currentLevel = skillInstance.get().getLevel(registryAccess);

        List<RenderableScreenElement> text = new ArrayList<>();
        for (int i = currentLevel - 5; i < currentLevel + 10; i++) {
            if (!rewardsConfiguration.hasRewardForLevel(i)) {
                continue;
            } else {
                Optional<SkillLevelUpRewardsData> rewards = rewardsConfiguration.tryGetRewardForLevel(i);
                if (rewards.isPresent()) {
                    boolean isUnlocked = i <= currentLevel;
                    TextComponentScreenElement levelText = new TextComponentScreenElement(
                            this.screen,
                            Settings.DEFAULT,
                            isUnlocked ? new Color(255, 170, 0) : Color.GRAY,
                            skillInstance.get().getRewardDescription(registryAccess, i, false, true, false)
                    );
                    TextComponentScreenElement rewardText = new TextComponentScreenElement(
                            this.screen,
                            Settings.DEFAULT,
                            isUnlocked ? Color.GREEN : Color.GRAY,
                            skillInstance.get().getRewardDescription(registryAccess, i, false, false, true)
                    );
                    levelText.setCurrentScale(0.5F);
                    rewardText.setCurrentScale(0.5F);
                    text.add(new HorizontalListScreenElement<>(this.screen, Settings.DEFAULT, YAlignment.CENTER, levelText, rewardText));
                }
            }
        }
        return text;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.render(graphics, partialTick, mouseX, mouseY);
    }
}

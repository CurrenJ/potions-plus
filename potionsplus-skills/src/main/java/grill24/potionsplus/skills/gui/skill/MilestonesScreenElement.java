package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.Milestone;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MilestonesScreenElement extends HorizontalListScreenElement<MilestoneScreenElement> {
    public MilestonesScreenElement(Screen screen, Settings settings, YAlignment alignment, MilestoneScreenElement... elements) {
        super(screen, settings, alignment, elements);
    }

    public void setMilestones(List<Milestone> milestones) {
        List<MilestoneScreenElement> elements = new ArrayList<>();
        for (int i = 0; i < milestones.size(); i++) {
            Milestone milestone = milestones.get(i);
            MilestoneScreenElement element = new MilestoneScreenElement(this.screen, this, this.settings, milestone, i * 2);
            elements.add(element);
        }

        this.setChildren(elements);
    }

    public void setSelectedSkill(ResourceKey<ConfiguredSkill<?, ?>> skill) {
        if (skill == null) {
            this.setMilestones(List.of());
            return;
        }

        Player player = this.screen.getMinecraft().player;
        if (player == null) {
            return;
        }
        RegistryAccess registryAccess = player.registryAccess();
        HolderGetter<ConfiguredSkill<?, ?>> skillRegistry = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL);
        Optional<Holder.Reference<ConfiguredSkill<?, ?>>> skillHolder = skillRegistry.get(skill);
        if (skillHolder.isPresent()) {
            ConfiguredSkill<?, ?> configuredSkill = skillHolder.get().value();
            this.setMilestones(configuredSkill.config().getData().milestones());
        }
    }
}

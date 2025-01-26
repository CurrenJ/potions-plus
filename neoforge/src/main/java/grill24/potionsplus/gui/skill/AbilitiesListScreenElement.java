package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;

import java.util.*;
import java.util.List;

public class AbilitiesListScreenElement extends VerticalListScreenElement<AbilityTextScreenElement> {
    private ResourceKey<ConfiguredSkill<?, ?>> selectedSkill;

    public AbilitiesListScreenElement(Screen screen, Settings settings) {
        super(screen, settings, XAlignment.CENTER, 0.5F);

        this.selectedSkill = null;
        setChildren(createAbilityDisplays());
    }

    private List<AbilityTextScreenElement> createAbilityDisplays() {
        // Nothing to display when no skill is selected
        if (selectedSkill == null) {
            return Collections.emptyList();
        }

        // Get all unlocked abilities for currently selected skill
        Collection<AbilityInstanceSerializable<?, ?>> abilities = getAbilities().stream().filter(
                abilityInstance -> Objects.equals(abilityInstance.data().getConfiguredAbility().config().getData().parentSkill().getKey(), selectedSkill)).toList();

        // Create type displays from unlocked abilities
        return abilities.stream().map(abilityInstance ->
                new AbilityTextScreenElement(
                        screen,
                        abilityInstance.data().getHolder().getKey().location(),
                        abilityInstance.data().getDescription()
                )
        ).toList();
    }

    public void setSelectedSkill(ResourceKey<ConfiguredSkill<?, ?>> selectedSkill) {
        this.selectedSkill = selectedSkill;
        this.setChildren(createAbilityDisplays());
    }

    private Collection<AbilityInstanceSerializable<?, ?>> getAbilities() {
        if (this.screen.getMinecraft().player == null) {
            return Collections.emptyList();
        }
        return SkillsData.getPlayerData(this.screen.getMinecraft().player).activeAbilities().values().stream().flatMap(List::stream).toList();
    }
}

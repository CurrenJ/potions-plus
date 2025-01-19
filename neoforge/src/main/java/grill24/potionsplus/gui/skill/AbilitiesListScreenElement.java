package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.AbilityInstance;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.List;

public class AbilitiesListScreenElement extends VerticalListScreenElement<AbilityTextScreenElement> {
    private ResourceKey<ConfiguredSkill<?, ?>> selectedSkill;

    public AbilitiesListScreenElement(Screen screen, Settings settings) {
        super(screen, settings, XAlignment.CENTER);

        this.selectedSkill = null;
        setChildren(createAbilityDisplays());
    }

    @Override
    protected void onTick(float partialTicks, int mouseX, int mouseY) {
        Player player = this.screen.getMinecraft().player;
        if (player == null) {
            return;
        }
        RegistryAccess registryAccess = player.registryAccess();

        // Every second we need to refresh the AbilityInstance references. This is because when we sync the SkillsData to the client, we replace the AbilityInstance instances with those received from the server.
        // TODO: Change data syncing to preserve existing references to ability instances?
        if (ClientTickHandler.total() % 20 == 0) {
            for (AbilityTextScreenElement ability : getChildren()) {
                ResourceKey<ConfiguredPlayerAbility<?, ?>> key = ability.getAbility().getHolder().getKey();
                SkillsData.getPlayerData(player).getAbilityInstance(registryAccess, key.location()).ifPresent(ability::setAbility);
            }
        }

        super.onTick(partialTicks, mouseX, mouseY);
    }

    private List<AbilityTextScreenElement> createAbilityDisplays() {
        // Nothing to display when no skill is selected
        if (selectedSkill == null) {
            return Collections.emptyList();
        }

        // Get all unlocked abilities for currently selected skill
        Collection<AbilityInstance> abilities = getAbilities().stream().filter(
                abilityInstance -> Objects.equals(abilityInstance.getConfiguredAbility().config().getData().parentSkill().getKey(), selectedSkill)).toList();

        // Create ability displays from unlocked abilities
        return abilities.stream().map(abilityInstance ->
                new AbilityTextScreenElement(
                        screen,
                        abilityInstance,
                        abilityInstance.getDescription()
                )
        ).toList();
    }

    public void setSelectedSkill(ResourceKey<ConfiguredSkill<?, ?>> selectedSkill) {
        this.selectedSkill = selectedSkill;
        this.setChildren(createAbilityDisplays());
    }

    private Collection<AbilityInstance> getAbilities() {
        if (this.screen.getMinecraft().player == null) {
            return Collections.emptyList();
        }
        return SkillsData.getPlayerData(this.screen.getMinecraft().player).activeAbilities().values().stream().flatMap(List::stream).toList();
    }
}

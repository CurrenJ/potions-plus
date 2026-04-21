package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.extension.IPlayerExtension;
import grill24.potionsplus.network.ServerboundSpawnDoubleJumpParticlesPacket;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.DoubleJumpAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Ability that allows the player to jump a second time in midair.
 * Does not implement {@link ITriggerablePlayerAbility} because Minecraft's movement is driven by the client,
 * so unlike most other abilities, the server cannot trigger the ability.
 * {@link ITriggerablePlayerAbility} is used only for abilities that are triggered by the server.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DoubleJumpAbility extends SimplePlayerAbility {
    public DoubleJumpAbility() {
        super(Set.of(AbilityInstanceTypes.DOUBLE_JUMP.value()));
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.DOUBLE_JUMP.value(),
                new DoubleJumpAbilityInstanceData(ability, true));
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        List<List<Component>> components = new ArrayList<>();

        List<Component> abilityTag = List.of(getRichEnablementTooltipComponent(instance.data().isEnabled()), Component.literal(" "), Component.translatable(Translations.TOOLTIP_POTIONSPLUS_ABILITY_TAG));
        components.add(abilityTag);
        components.add(List.of());

        Optional<List<List<Component>>> component = super.getLongDescription(instance, config, params);
        component.ifPresent(components::addAll);

        return Optional.of(components);
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(final MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        long gameTime = 0L;
        try (Level level = player.level()) {
            gameTime = level.getGameTime();
        } catch (IOException ignored) {
        }

        SkillsData skillsData = SkillsData.getPlayerData(player);
        Optional<AbilityInstanceSerializable<?, ?>> inst = skillsData.getAbilityInstance(player.registryAccess(), ConfiguredPlayerAbilities.DOUBLE_JUMP.getKey());
        if (inst.isPresent() && inst.get().data().isEnabled() && inst.get().data() instanceof DoubleJumpAbilityInstanceData data) {
            if (player.onGround()) {
                data.resetJumps();
            }
            if (event.getInput().keyPresses.jump() && player.isLocalPlayer() && data.hasFinishedCooldown(gameTime) && data.getJumpsLeft() > 0) {
                if (player.onGround()) {
                    data.onInitialJump(gameTime);
                } else {
                    ((IPlayerExtension) player).potions_plus$performAdditionalJump();
                    data.decrementJumps(gameTime);
                    PacketDistributor.sendToServer(new ServerboundSpawnDoubleJumpParticlesPacket(player.position()));
                }
            }
        }
    }
}

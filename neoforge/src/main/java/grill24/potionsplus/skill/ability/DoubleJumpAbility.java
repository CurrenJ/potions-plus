package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.*;
import grill24.potionsplus.extension.IPlayerExtension;
import grill24.potionsplus.network.ServerboundSpawnDoubleJumpParticlesPacket;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.DoubleJumpAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

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

    public static class Builder implements ConfiguredPlayerAbilities.IAbilityBuilder {
        ResourceKey<ConfiguredPlayerAbility<?, ?>> key;
        String translationKey;

        ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey;

        public Builder(String name) {
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ppId(name));

            this.translationKey = "";
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder parentSkill(ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey) {
            this.parentSkillKey = parentSkillKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (parentSkillKey == null) {
                throw new IllegalStateException("Parent skill must be set");
            }

            HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

            context.register(key,
                    new ConfiguredPlayerAbility<>(PlayerAbilities.DOUBLE_JUMP.value(),
                            new PlayerAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(parentSkillKey))))
            );
        }

        @Override
        public ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey() {
            return key;
        }
    }

    public static void onJumpFromGround(Player player) {
        SkillsData skillsData = SkillsData.getPlayerData(player);
        Optional<AbilityInstanceSerializable<?, ?>> inst = skillsData.getAbilityInstance(player.registryAccess(), ConfiguredPlayerAbilities.DOUBLE_JUMP.getKey());
        if(inst.isPresent() && inst.get().data() instanceof DoubleJumpAbilityInstanceData data && player.isLocalPlayer()) {
            try (Level level = player.level()) {
                data.onInitialJump(level.getGameTime());
            } catch (IOException ignored) {}
        }
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(final MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        long gameTime = 0L;
        try (Level level = player.level()) {
            gameTime = level.getGameTime();
        } catch (IOException ignored) {}

        SkillsData skillsData = SkillsData.getPlayerData(player);
        Optional<AbilityInstanceSerializable<?, ?>> inst = skillsData.getAbilityInstance(player.registryAccess(), ConfiguredPlayerAbilities.DOUBLE_JUMP.getKey());
        if (inst.isPresent() && inst.get().data().isEnabled() && inst.get().data() instanceof DoubleJumpAbilityInstanceData data) {
            if (player.onGround()) {
                data.resetJumps();
            }
            if (event.getInput().jumping && player.isLocalPlayer() && data.hasFinishedCooldown(gameTime) && data.getJumpsLeft() > 0){
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

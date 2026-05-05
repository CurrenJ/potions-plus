package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.skill.ability.CriticalHitData;
import grill24.potionsplus.skill.ability.HotPotatoAbility;
import grill24.potionsplus.skill.ability.LastBreathAbility;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class AbilityListeners {
    @SubscribeEvent
    public static void onCriticalHitChainLightning(final CriticalHitEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.CHAIN_LIGHTNING.value().triggerFromServer(
                    serverPlayer, ConfiguredPlayerAbilities.CHAIN_LIGHTNING.getKey(),
                    new CriticalHitData(event.getTarget()));
        }
    }

    @SubscribeEvent
    public static void onCriticalHitStunShot(final CriticalHitEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.STUN_SHOT.value().triggerFromServer(
                    serverPlayer, ConfiguredPlayerAbilities.STUN_SHOT.getKey(),
                    new CriticalHitData(event.getTarget()));
        }
    }

    @SubscribeEvent
    public static void onLivingDamageHotPotato(final LivingDamageEvent.Pre event) {
        DamageSource damage = event.getSource();
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && (damage.is(DamageTypes.LAVA)
                || damage.is(DamageTypes.FIREBALL)
                || damage.is(DamageTypes.IN_FIRE)
                || damage.is(DamageTypes.ON_FIRE)
                || damage.is(DamageTypes.CAMPFIRE))) {
            PlayerAbilities.HOT_POTATO.value().triggerFromServer(
                    serverPlayer, ConfiguredPlayerAbilities.HOT_POTATO.getKey(),
                    new HotPotatoAbility.FireDamageData(damage, () -> event.setNewDamage(0)));
        }
    }

    @SubscribeEvent
    public static void onLivingDrownLastBreath(final LivingDrownEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.LAST_BREATH.value().triggerFromServer(
                    serverPlayer, ConfiguredPlayerAbilities.LAST_BREATH.getKey(),
                    new LastBreathAbility.DrownData());
        }
    }
}

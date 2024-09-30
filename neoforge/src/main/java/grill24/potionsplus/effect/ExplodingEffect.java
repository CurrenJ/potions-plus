package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.network.ClientboundImpulsePlayerPacket;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ExplodingEffect extends MobEffect {
    public ExplodingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Expired potionExpiryEvent) {
        LivingEntity entity = potionExpiryEvent.getEntity();

        if (!entity.level().isClientSide && Objects.requireNonNull(potionExpiryEvent.getEffectInstance()).getEffect() == MobEffects.EXPLODING) {
            boolean isPlayer = entity instanceof Player;
            int amplifier = potionExpiryEvent.getEffectInstance().getAmplifier() + 1;

            Level.ExplosionInteraction blockInteraction = isPlayer ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.BLOCK;
            entity.level().explode(isPlayer ? entity : null, entity.getRandomX(0.1), entity.getY() + 0.5, entity.getRandomZ(0.1), 5.0F * amplifier, blockInteraction);

            if (isPlayer) {
                entity.setHealth(entity.getHealth() - 1.5F * amplifier);
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);

                Vec3 velocity = entity.getLookAngle().scale(3).multiply(2, 0.5, 2).multiply(amplifier, amplifier, amplifier);

                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) entity.level(), entity.level().getChunkAt(entity.blockPosition()).getPos(), new ClientboundImpulsePlayerPacket(velocity.x, velocity.y, velocity.z));
            }
        }
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        if (Objects.requireNonNull(potionAddedEvent.getEffectInstance()).getEffect() == MobEffects.EXPLODING) {
            LivingEntity entity = potionAddedEvent.getEntity();
            Vec3 pos = entity.position();
            entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}

package grill24.potionsplus.effect;

import grill24.potionsplus.core.ClientboundImpulsePlayerPacket;
import grill24.potionsplus.core.MobEffects;
import grill24.potionsplus.core.PotionsPlusPacketHandler;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplodingEffect extends MobEffect {
    public ExplodingEffect(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @SubscribeEvent
    public static void onPotionExpiry(final PotionEvent.PotionExpiryEvent potionExpiryEvent) {
        LivingEntity entity = potionExpiryEvent.getEntityLiving();

        if (!entity.level.isClientSide && Objects.requireNonNull(potionExpiryEvent.getPotionEffect()).getEffect() == MobEffects.EXPLODING.get()) {
            boolean isPlayer = entity instanceof Player;

            Explosion.BlockInteraction blockInteraction = isPlayer ? Explosion.BlockInteraction.NONE : Explosion.BlockInteraction.BREAK;
            entity.level.explode(null, entity.getRandomX(0.1), entity.getY() + 0.5, entity.getRandomZ(0.1), 5.0F, blockInteraction);

            if (isPlayer) {
                Vec3 velocity = entity.getLookAngle().scale(3).multiply(2, 0.5, 2);
                PotionsPlusPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> entity.level.getChunkAt(entity.blockPosition())), new ClientboundImpulsePlayerPacket(velocity.x, velocity.y, velocity.z));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEntityDamage(final LivingDamageEvent livingDamageEvent) {
        LivingEntity entity = livingDamageEvent.getEntityLiving();
        if (entity instanceof Player && livingDamageEvent.getSource().isExplosion()) {
            if (entity.hasEffect(MobEffects.EXPLODING.get())) {
                livingDamageEvent.setCanceled(true);
                entity.hurt(DamageSource.explosion(entity), 4.0F);
            }
        }
    }

    @SubscribeEvent
    public static void onUsePotion(final PotionEvent.PotionAddedEvent potionAddedEvent) {
        if (Objects.requireNonNull(potionAddedEvent.getPotionEffect()).getEffect() == MobEffects.EXPLODING.get()) {
            LivingEntity entity = potionAddedEvent.getEntityLiving();
            Vec3 pos = entity.position();
            entity.level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}

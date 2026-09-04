package grill24.potionsplus.platform.neoforge;

import grill24.potionsplus.config.PotionsPlusConfig;
import grill24.potionsplus.event.neoforge.ServerPlayerHeldItemChangedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class PlatformImpl {
    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        // DIVERGENCE from 26.1.2: NeoForge 21.1.209 has no EventHooks.onItemConsumptionTeleport /
        // EntityTeleportEvent.ItemConsumption. The item-consumption teleport hook only exists in newer
        // NeoForge. 21.1's equivalent fire point is the chorus-fruit entity teleport event, which is
        // what this branch's TeleportationEffect already used. Keep the common signature identical for
        // diffability (Decision 4); the stack argument is unused here.
        EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(entity, x, y, z);
        return new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, player, previousItem, newItem));
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        CommonHooks.fireCropGrowPost(level, pos, state);
    }

    public static int getPotionDrinkTimeTicks() {
        // Recipe building can run during datagen, before the server config is loaded - fall back to
        // the configured default in that case rather than letting ModConfigSpec throw.
        try {
            return PotionsPlusConfig.CONFIG.potionDrinkTimeTicks.get();
        } catch (IllegalStateException e) {
            return PotionsPlusConfig.CONFIG.potionDrinkTimeTicks.getDefault();
        }
    }

    public static int getPotionDrinkCooldownTimeTicks() {
        try {
            return PotionsPlusConfig.CONFIG.potionDrinkCooldownTimeTicks.get();
        } catch (IllegalStateException e) {
            return PotionsPlusConfig.CONFIG.potionDrinkCooldownTimeTicks.getDefault();
        }
    }
}

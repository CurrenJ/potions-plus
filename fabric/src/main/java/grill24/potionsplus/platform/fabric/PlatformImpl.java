package grill24.potionsplus.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import grill24.potionsplus.config.fabric.PotionsPlusConfig;

public class PlatformImpl {
    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        // NeoForge fires EntityTeleportEvent.ItemConsumption here for third-party interop; potionsplus
        // itself has no subscriber for it (Phase 4 confirmed the mirror ServerPlayerHeldItemChangedEvent
        // is dead code), so a plain passthrough is behaviorally identical for this mod.
        return new Vec3(x, y, z);
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        // No-op: NeoForge posts a custom event here with zero subscribers (see above).
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        // No-op: NeoForge fires BlockEvent.CropGrowEvent.Post here for third-party interop only.
    }

    public static int getPotionDrinkTimeTicks() {
        return PotionsPlusConfig.CONFIG.potionDrinkTimeTicks;
    }

    public static int getPotionDrinkCooldownTimeTicks() {
        return PotionsPlusConfig.CONFIG.potionDrinkCooldownTimeTicks;
    }
}

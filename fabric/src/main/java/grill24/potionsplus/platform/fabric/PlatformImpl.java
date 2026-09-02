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

public class PlatformImpl {
    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        // Fabric fires no chorus-fruit teleport event (NeoForge/Forge fire EntityTeleportEvent
        // natively before teleporting); the common TeleportationEffect only needs the coordinates,
        // so return them unmodified.
        return new Vec3(x, y, z);
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        // PHASE 7 (event surface): no-op until the held-item-change listener is ported.
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        // PHASE 7 (event surface): no-op until the crop-grow listener is ported. Fabric has no
        // equivalent of Forge's BlockEvent.CropGrowEvent.Post to fire for third-party interop.
    }

    public static int getPotionDrinkTimeTicks() {
        // PHASE 8 (config): hardcoded to the NeoForge default until a cross-loader config exists.
        return 16;
    }

    public static int getPotionDrinkCooldownTimeTicks() {
        // PHASE 8 (config): hardcoded to the NeoForge default until a cross-loader config exists.
        return 0;
    }
}

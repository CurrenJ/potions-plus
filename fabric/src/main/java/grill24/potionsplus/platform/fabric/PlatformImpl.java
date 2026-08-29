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
        // Stub: Fabric chorus-fruit teleport hook lands in Phase 5.
        return new Vec3(x, y, z);
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        // Stub: Fabric held-item-changed wiring lands in Phase 5.
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        // Stub: Fabric crop-grow wiring lands in Phase 5.
    }

    public static int getPotionDrinkTimeTicks() {
        // Stub: Fabric server config lands in Phase 5; return the NeoForge default.
        return 16;
    }

    public static int getPotionDrinkCooldownTimeTicks() {
        // Stub: Fabric server config lands in Phase 5; return the NeoForge default.
        return 0;
    }
}

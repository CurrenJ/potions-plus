package grill24.potionsplus.platform.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

public class PlatformImpl {
    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        // Stub: Forge chorus-fruit teleport event hook lands in Phase 5.
        return new Vec3(x, y, z);
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        // Stub: Forge held-item-changed event wiring lands in Phase 5.
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        // Stub: Forge crop-grow event wiring lands in Phase 5.
    }

    public static int getPotionDrinkTimeTicks() {
        // Stub: Forge server config lands in Phase 5; return the NeoForge default.
        return 16;
    }

    public static int getPotionDrinkCooldownTimeTicks() {
        // Stub: Forge server config lands in Phase 5; return the NeoForge default.
        return 0;
    }
}

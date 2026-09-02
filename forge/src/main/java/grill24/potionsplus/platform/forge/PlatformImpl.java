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

public class PlatformImpl {
    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        // Forge 52.x fires EntityTeleportEvent.ChorusFruit natively in ChorusFruitItem before
        // teleporting, so the common TeleportationEffect target needs no re-fire here. Returning the
        // target unmodified (vanilla).
        return new Vec3(x, y, z);
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        // PHASE 7 (event surface): no-op until the held-item-change listener is ported.
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        // PHASE 7 (event surface): no-op until the crop-grow listener is ported. Forge 52.x's
        // ForgeEventFactory has no fireCropGrowPost(Level, BlockPos, BlockState) — verify the exact
        // 52.x hook name when wiring the listener.
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

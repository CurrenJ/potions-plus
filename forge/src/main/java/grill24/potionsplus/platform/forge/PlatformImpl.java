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
import grill24.potionsplus.config.forge.PotionsPlusConfig;

public class PlatformImpl {
    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
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
        // Recipe building can run during datagen, before the server config is loaded - fall back to
        // the configured default in that case rather than letting ForgeConfigSpec throw.
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

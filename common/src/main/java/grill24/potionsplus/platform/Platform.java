package grill24.potionsplus.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Platform {
    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        throw new AssertionError();
    }

    /** Configured drink time for brewed potions, in ticks. Server-admin-configurable. */
    @ExpectPlatform
    public static int getPotionDrinkTimeTicks() {
        throw new AssertionError();
    }

    /** Configured post-drink use-cooldown for brewed potions, in ticks. Server-admin-configurable. */
    @ExpectPlatform
    public static int getPotionDrinkCooldownTimeTicks() {
        throw new AssertionError();
    }
}

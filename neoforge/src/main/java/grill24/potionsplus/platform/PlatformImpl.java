package grill24.potionsplus.platform;

import grill24.potionsplus.event.ServerPlayerHeldItemChangedEvent;
import grill24.potionsplus.event.runtimeresource.ClientInjectResourceStacksEvent;
import grill24.potionsplus.event.runtimeresource.ClientInjectResourcesEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.List;
import java.util.Map;

public class PlatformImpl {
    public static boolean isClient() {
        return FMLEnvironment.getDist() == Dist.CLIENT;
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    public static Vec3 getChorusFruitTeleportTarget(LivingEntity entity, ItemStack stack, double x, double y, double z) {
        EntityTeleportEvent.ItemConsumption event = EventHooks.onItemConsumptionTeleport(entity, stack, x, y, z);
        return new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }

    public static void onServerPlayerHeldItemChanged(MinecraftServer server, ServerPlayer player, ItemStack previousItem, ItemStack newItem) {
        NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(server, player, previousItem, newItem));
    }

    public static void fireCropGrowPost(Level level, BlockPos pos, BlockState state) {
        CommonHooks.fireCropGrowPost(level, pos, state);
    }

    public static void postClientInjectResourcesEvent(Map<Identifier, Resource> resources) {
        ModLoader.postEvent(new ClientInjectResourcesEvent(resources));
    }

    public static void postClientInjectResourceStacksEvent(Map<Identifier, List<Resource>> resources) {
        ModLoader.postEvent(new ClientInjectResourceStacksEvent(resources));
    }
}

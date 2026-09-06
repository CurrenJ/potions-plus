package grill24.potionsplus.core.forge;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * Forge equivalent of {@code core.neoforge.Capabilities} (Phase 8 "Capabilities / IItemHandler"
 * bucket) - the Clothesline block's item storage, exposed as {@code IItemHandler} so hoppers/pipes
 * can interact with it.
 *
 * <p><b>1.21.1 Forge 52.1.2 still uses the pre-1.20.5 capability-provider shape</b> - verified via
 * {@code javap} against {@code forge-1.21.1-52.1.2-universal-srg.jar} before writing this (do not
 * assume 26.1.2's {@code RegisterCapabilitiesEvent}/{@code BlockCapability} API, which does not
 * exist here): {@code net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent} in this jar
 * is the *old* per-mod {@code <T> void register(Class<T>)} declaration event, not NeoForge's
 * lookup-registration event, and there is no {@code net.minecraftforge.capabilities} package at
 * all. The real hook is {@code net.minecraftforge.event.AttachCapabilitiesEvent<BlockEntity>}
 * (confirmed via javap: {@code public T getObject(); public void addCapability(ResourceLocation,
 * ICapabilityProvider); public void addListener(Runnable)}), fired on the game bus once per
 * {@code BlockEntity} instance - {@code javap} on the forge-patched
 * {@code minecraft-merged-srg-patched.jar}'s {@code BlockEntity.class} confirms it {@code extends
 * CapabilityProvider<BlockEntity>} already, so no extra provider plumbing on the BE side is needed.
 * {@code ICapabilityProvider.getCapability(Capability<T>, Direction)} returns a
 * {@code LazyOptional<T>} (not a plain nullable value like NeoForge's lookup) - confirmed via javap.
 * {@code ForgeCapabilities.ITEM_HANDLER} (javap-confirmed present, mirroring NeoForge's
 * {@code Capabilities.ITEM_HANDLER} constant) is the well-known capability instance every
 * item-handler consumer (hoppers, pipes, other mods) already queries, so this reuses it instead of
 * declaring a new named capability the way NeoForge's {@code BlockCapability.createSided(...)}
 * requires. {@code net.minecraftforge.items.wrapper.InvWrapper} lives at the *same* package path as
 * NeoForge's ({@code items.wrapper.InvWrapper(Container)} - javap-confirmed identical constructor
 * signature), so the wrapping logic is a direct port of {@code core.neoforge.Capabilities}.
 *
 * <p>Matches this module's {@code CommandListeners}/{@code TickListeners}/{@code EffectListeners}
 * explicit-registration style (a plain lambda against {@link MinecraftForge#EVENT_BUS}, not an
 * {@code @Mod.EventBusSubscriber} class) - {@code register()} is called once from
 * {@code PotionsPlusForge}'s constructor.
 */
public final class Capabilities {
    private Capabilities() {
    }

    public static void register() {
        // AttachCapabilitiesEvent<T> extends GenericEvent<T> - IEventBus's plain addListener rejects
        // generic events outright (IllegalArgumentException: "Cannot register a generic event
        // listener with addListener, use addGenericListener"), confirmed by an actual :forge:runServer
        // crash before this fix. addGenericListener(Class<F>, Consumer<T>) is the correct overload
        // (javap-verified against eventbus-6.2.32.jar, the version this Forge 52.1.2 pulls in).
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, Capabilities::attachClotheslineItemHandler);
    }

    @SuppressWarnings("unchecked")
    private static void attachClotheslineItemHandler(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!(event.getObject() instanceof ClotheslineBlockEntity clotheslineBlockEntity)) {
            return;
        }

        event.addCapability(grill24.potionsplus.utility.Utility.ppId("clothesline_item_handler"),
                new ICapabilityProvider() {
                    @Nullable
                    @Override
                    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
                        if (capability != ForgeCapabilities.ITEM_HANDLER) {
                            return LazyOptional.empty();
                        }

                        Level level = clotheslineBlockEntity.getLevel();
                        BlockPos pos = clotheslineBlockEntity.getBlockPos();
                        BlockState state = clotheslineBlockEntity.getBlockState();
                        if (level == null) {
                            return LazyOptional.empty();
                        }

                        BlockEntity leftBlockEntity = level.getBlockEntity(ClotheslineBlock.getLeftEnd(pos, state));
                        if (!(leftBlockEntity instanceof Container container)) {
                            return LazyOptional.empty();
                        }

                        LazyOptional<IItemHandler> lazyHandler = LazyOptional.of(() -> new InvWrapper(container));
                        return (LazyOptional<T>) lazyHandler;
                    }
                });
    }
}

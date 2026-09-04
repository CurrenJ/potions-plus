package grill24.potionsplus.core.fabric;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fabric equivalent of {@code core.neoforge.Capabilities} (Phase 8 "Capabilities / IItemHandler"
 * bucket) - the Clothesline block's item storage, exposed via {@code fabric-transfer-api-v1} so
 * hoppers/pipes can interact with it.
 *
 * <p>This branch is pinned to {@code fabric_api_version = 0.116.7+1.21.1} (Decision 4's toolchain
 * table); that resolves {@code fabric-transfer-api-v1:5.4.3+c24bd99419} (read straight off
 * {@code fabric-api-0.116.7+1.21.1.pom}'s own dependency list, not assumed) - verified via javap
 * against that exact jar before writing this, not the plan's 26.1.2-era text. Confirms the plan's
 * 1.21.1-era name is right and 26.1.2's is stale: {@code ItemStorage.SIDED} is a
 * {@code BlockApiLookup<Storage<ItemVariant>, Direction>} constant (there is no
 * {@code ContainerStorage} class in this jar at all - that was a pre-transfer-api name, already
 * superseded by this version). {@code BlockApiLookup} itself lives in the separate
 * {@code fabric-api-lookup-api-v1:1.6.71+b559734419} module (also read off the same pom, not
 * guessed) and javap confirms the exact overload used here:
 * {@code registerForBlockEntity(BiFunction<? super T, C, A>, BlockEntityType<T>)}. javap on
 * {@code InventoryStorage} confirms {@code static InventoryStorage of(Container, Direction)} -
 * matching the plan's "not {@code ContainerStorage.of}" note (26.1.2 used that older name; this
 * fabric-api version already renamed it to {@code InventoryStorage.of}).
 */
public final class Capabilities {
    private Capabilities() {
    }

    public static void register() {
        ItemStorage.SIDED.registerForBlockEntity(
                (ClotheslineBlockEntity blockEntity, net.minecraft.core.Direction direction) -> {
                    Level level = blockEntity.getLevel();
                    if (level == null) {
                        return null;
                    }

                    BlockPos pos = blockEntity.getBlockPos();
                    BlockState state = blockEntity.getBlockState();
                    BlockEntity leftBlockEntity = level.getBlockEntity(ClotheslineBlock.getLeftEnd(pos, state));
                    if (leftBlockEntity instanceof Container container) {
                        return InventoryStorage.of(container, direction);
                    }
                    return null;
                },
                grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY.value());
    }
}

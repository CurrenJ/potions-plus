package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3d;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SanguineAltarBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {

    private int timeItemPlaced;
    public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5);
    private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);

    private int nextSpinStartTick = -1;
    private int nextSpinTotalRevolutions = 0;
    private float nextSpinHertz = 0;

    public SanguineAltarBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected SimpleContainer createItemHandler() {
        return new SimpleContainer(1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SanguineAltarBlockEntity blockEntity) {
        if (blockEntity.nextSpinStartTick < ClientTickHandler.total() - 100) {
            blockEntity.nextSpinStartTick = (int) ClientTickHandler.total() + level.getRandom().nextInt(0, 300);
            blockEntity.nextSpinTotalRevolutions = level.getRandom().nextInt(1, 3);
            blockEntity.nextSpinHertz = level.getRandom().nextFloat(0.25F, 1F);
        }

        // particles
        if (level.isClientSide && !blockEntity.getItem(0).isEmpty()) {
            Vec3 posVec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            for (int i = 0; i < 1; i++) {
                if (level.random.nextDouble() < 0.1) {
                    Vec3 particlePos = posVec.add(level.random.nextGaussian(0, 0.5), 0.25 + level.random.nextDouble(-0.125, 0.125), level.random.nextGaussian(0, 0.5));
                    level.addParticle(ParticleTypes.PORTAL, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public int getTimeItemPlaced() {
        return timeItemPlaced;
    }

    @Override
    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(itemAnimationStartingPosRelativeToBlockOrigin.x, itemAnimationStartingPosRelativeToBlockOrigin.y, itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return itemRestingPositionTranslation;
    }

    public void onPlayerInsertItem(Player player) {
        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        timeItemPlaced = ((int) ClientTickHandler.total());
    }

    public int getNextSpinTickDelay() {
        return nextSpinStartTick - timeItemPlaced;
    }

    public int getNextSpinTotalRevolutions() {
        return nextSpinTotalRevolutions;
    }

    public float getNextSpinHertz() {
        return nextSpinHertz;
    }
}

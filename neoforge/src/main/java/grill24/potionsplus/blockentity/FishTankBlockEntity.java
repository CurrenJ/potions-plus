package grill24.potionsplus.blockentity;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FishTankBlockEntity extends InventoryBlockEntity {
    Kelp[] kelpList;
    private Direction fishFacing;
    private boolean horizontalFlip;

    private static final float SAND_HEIGHT = 2 / 16F;
    private static final float BASE_KELP_SIZE = 0.2F;
    private static final float KELP_CENTER_X = 0.5F - BASE_KELP_SIZE / 2F;
    private static final float KELP_CENTER_Z = 0.5F - BASE_KELP_SIZE / 2F;

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.FISH_TANK_BLOCK_ENTITY.value(), pos, state);

        kelpList = generateKelp(new Random(getBlockPos().hashCode()));
        fishFacing = Direction.NORTH;
        horizontalFlip = false;
    }

    private static Kelp[] generateKelp(Random random) {
        int radius = 2;
        List<Kelp> kelpList = new ArrayList<>();
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                Random kelpRandom = new Random((long) (random.nextFloat() + i * 1000L + j * 1000L));
                Kelp kelp = new Kelp(new Vector3f(KELP_CENTER_X + i * BASE_KELP_SIZE, SAND_HEIGHT, KELP_CENTER_Z + j * BASE_KELP_SIZE), BASE_KELP_SIZE, kelpRandom.nextInt(4) + 1);
                kelpList.add(kelp);
            }
        }

        // Randomize order of kelp
        for (int i = kelpList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Kelp temp = kelpList.get(i);
            kelpList.set(i, kelpList.get(j));
            kelpList.set(j, temp);
        }

        return kelpList.toArray(new Kelp[0]);
    }

    @Override
    protected int getSlots() {
        return 5;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if ((stack.has(DataComponents.FISH_SIZE) || stack.is(Tags.Items.BAIT) && index == 0) ||
                (stack.is(Items.KELP) && index == 1)) {
            return super.canPlaceItem(index, stack);
        }

        return false;
    }

    public void onItemInserted(Player player, ItemStack itemStack) {
        if (player != null) {
            // Get direction between player and fish tank
            Vec3 pos = player.position();
            Vec3 fishTankPos = Vec3.atCenterOf(getBlockPos());
            Vec3 direction = fishTankPos.subtract(pos).normalize();
            // Get the direction of the fish tank
            Direction fishTankDirection = Direction.getNearest(direction.x, 0, direction.z);
            // Get the opposite direction of the fish tank
            fishFacing = fishTankDirection.getOpposite();

            horizontalFlip = player.getRandom().nextBoolean();

            PotionsPlus.LOGGER.info("Fish tank facing: " + fishFacing);
        }

        super.setChanged();
    }

    public Direction getFishFacing() {
        return fishFacing;
    }

    public boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    public Optional<ItemStack> getFish() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.has(DataComponents.FISH_SIZE) || itemStack.is(Tags.Items.BAIT)) {
                return Optional.of(itemStack);
            }
        }
        return Optional.empty();
    }

    public int getKelp() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.is(Items.KELP)) {
                return itemStack.getCount();
            }
        }
        return 0;
    }

    public Kelp[] getKelpRenderData() {
        return kelpList;
    }

    public record Kelp(Vector3f pos, float size, int height) { }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        int facing = tag.getInt("fishFacing");
        fishFacing = Direction.from3DDataValue(facing);

        horizontalFlip = tag.getBoolean("horizontalFlip");
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        tag.putInt("fishFacing", fishFacing.get3DDataValue());

        tag.putBoolean("horizontalFlip", horizontalFlip);
    }
}

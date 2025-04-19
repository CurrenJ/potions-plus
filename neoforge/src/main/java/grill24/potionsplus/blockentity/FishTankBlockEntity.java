package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FishTankBlockEntity extends InventoryBlockEntity {
    Kelp[] kelpList;

    private static final float SAND_HEIGHT = 2 / 16F;
    private static final float BASE_KELP_SIZE = 0.2F;
    private static final float KELP_CENTER_X = 0.5F - BASE_KELP_SIZE / 2F;
    private static final float KELP_CENTER_Z = 0.5F - BASE_KELP_SIZE / 2F;

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.FISH_TANK_BLOCK_ENTITY.value(), pos, state);

        kelpList = generateKelp(new Random(getBlockPos().hashCode()));
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
}

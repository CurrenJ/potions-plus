package grill24.potionsplus.blockentity;

import grill24.potionsplus.block.FishTankBlock;
import grill24.potionsplus.block.FishTankFrameBlock;
import grill24.potionsplus.block.FishTankSandBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.item.FishSizeDataComponent;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.*;

public class FishTankBlockEntity extends InventoryBlockEntity {
    Kelp[] kelpList;
    private Direction fishFacing;
    private boolean horizontalFlip;

    private static final float SAND_HEIGHT = 2 / 16F;
    private static final float BASE_KELP_SIZE = 0.2F;
    private static final float KELP_CENTER_X = 0.5F - BASE_KELP_SIZE / 2F;
    private static final float KELP_CENTER_Z = 0.5F - BASE_KELP_SIZE / 2F;

    private ItemStack frameVariantBlockItem;
    private BlockState renderFrameState;

    private ItemStack sandVariantBlockItem;
    private BlockState renderSandState;

    private int faces;
    private static final int ALL_FACES = BlockEntityBlocks.getFishTankPartId(Map.of(
            Direction.NORTH, true,
            Direction.SOUTH, true,
            Direction.EAST, true,
            Direction.WEST, true,
            Direction.UP, true,
            Direction.DOWN, true
    ));

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.FISH_TANK_BLOCK_ENTITY.value(), pos, state);

        kelpList = generateKelp(new Random(getBlockPos().hashCode()));
        fishFacing = Direction.NORTH;
        horizontalFlip = false;

        frameVariantBlockItem = new ItemStack(Items.OAK_PLANKS);
        updateRenderStates(frameVariantBlockItem);
        sandVariantBlockItem = new ItemStack(Items.SAND);
        updateRenderStates(sandVariantBlockItem);


        faces = ALL_FACES;
        updateFaces();
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
            Direction fishTankDirection = Direction.getApproximateNearest(direction.x, 0, direction.z);
            // Get the opposite direction of the fish tank
            fishFacing = fishTankDirection.getOpposite();

            horizontalFlip = player.getRandom().nextBoolean();
        }

        // TODO: Make better
        FishSizeDataComponent fishSizeData = itemStack.get(DataComponents.FISH_SIZE);
//        if(fishSizeData != null && level != null) {
//            float size = fishSizeData.size();
//            if(size > 100F) {
//                BlockState left = level.getBlockState(getBlockPos().relative(fishFacing.getClockWise()));
//                BlockState right = level.getBlockState(getBlockPos().relative(fishFacing.getCounterClockWise()));
//                if (!(left.getBlock() instanceof FishTankBlock) || !(right.getBlock() instanceof FishTankBlock)) {
//                    // Spit out the item if there is no space for the fish
//                    // Find item in inventory and remove it
//                    for (int i = 0; i < getSlots(); i++) {
//                        if (getItem(i).is(itemStack.getItem())) {
//                            removeItem(i, 1);
//                            break;
//                        }
//                    }
//
//                    Containers.dropItemStack(level, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, itemStack);
//                    if (player != null) {
//                        player.displayClientMessage(Component.translatable("alert.fish_tank.too_small"), true);
//                    }
//                }
//            }
//        }

        this.setChanged();
    }

    public boolean updateRenderStates(ItemStack usedItem) {
        Holder<Block> blockHolder = BlockEntityBlocks.FISH_TANK_FRAME_BLOCKS.get(getFaces());
        final Optional<BlockState> frameState = RuntimeTextureVariantModelGenerator.tryGetTextureVariantBlockState(
                blockHolder.value(), usedItem, blockHolder.value().defaultBlockState(), FishTankFrameBlock.FRAME_VARIANT);
        if (frameState.isPresent() && this.renderFrameState != frameState.get()) {
            this.frameVariantBlockItem = usedItem;
            this.renderFrameState = frameState.get();
            this.setChanged();
            return true;
        }

        Holder<Block> sandHolder = BlockEntityBlocks.FISH_TANK_SAND_BLOCKS.get(getFaces());
        final Optional<BlockState> sandState = RuntimeTextureVariantModelGenerator.tryGetTextureVariantBlockState(
                sandHolder.value(), usedItem, sandHolder.value().defaultBlockState(), FishTankSandBlock.SAND_VARIANT);
        if (sandState.isPresent() && this.renderSandState != sandState.get()) {
            this.sandVariantBlockItem = usedItem;
            this.renderSandState = sandState.get();
            this.setChanged();
            return true;
        }

        return false;
    }

    public Optional<BlockState> getRenderFrameState() {
        return Optional.ofNullable(renderFrameState);
    }

    public Optional<BlockState> getRenderSandState() {
        return Optional.ofNullable(renderSandState);
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

    public record Kelp(Vector3f pos, float size, int height) {
    }


    public int getFaces() {
        return faces;
    }

    public void updateFaces() {
        if (level == null) {
            return;
        }

        Map<Direction, Boolean> faces = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            BlockState adjacentState = level.getBlockState(getBlockPos().relative(direction));
            if (adjacentState.is(BlockEntityBlocks.FISH_TANK)) {
                faces.put(direction, false);
            } else {
                faces.put(direction, true);
            }
        }
        this.faces = BlockEntityBlocks.getFishTankPartId(faces);

        this.setChanged();
    }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        int facing = tag.getInt("fishFacing").orElse(0);
        fishFacing = Direction.from3DDataValue(facing);
        horizontalFlip = tag.getBoolean("horizontalFlip").orElse(false);
        faces = tag.getInt("faces").orElse(ALL_FACES);

        frameVariantBlockItem = ItemStack.parse(registryAccess, tag.getCompoundOrEmpty("frameVariantBlockItem")).orElse(ItemStack.EMPTY);
        updateRenderStates(frameVariantBlockItem);
        sandVariantBlockItem = ItemStack.parse(registryAccess, tag.getCompoundOrEmpty("sandVariantBlockItem")).orElse(ItemStack.EMPTY);
        updateRenderStates(sandVariantBlockItem);
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        tag.putInt("fishFacing", fishFacing.get3DDataValue());
        tag.putBoolean("horizontalFlip", horizontalFlip);
        tag.putInt("faces", faces);

        if (frameVariantBlockItem != null && !frameVariantBlockItem.isEmpty()) {
            tag.put("frameVariantBlockItem", frameVariantBlockItem.save(registryAccess));
        }
        if (sandVariantBlockItem != null && !sandVariantBlockItem.isEmpty()) {
            tag.put("sandVariantBlockItem", sandVariantBlockItem.save(registryAccess));
        }
    }
}

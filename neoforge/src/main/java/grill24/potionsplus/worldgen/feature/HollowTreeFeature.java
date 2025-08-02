package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class HollowTreeFeature extends Feature<NoneFeatureConfiguration> {
    public HollowTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // Check if there's enough space for a giant tree (8x8 base, 12-15 blocks tall)
        int treeHeight = 12 + random.nextInt(4); // 12-15 blocks tall
        int baseRadius = 4; // 8x8 base (radius 4)
        int hollowRadius = 2; // 4x4 hollow center (radius 2)

        // Check if we have a suitable location
        if (!isValidLocation(level, pos, treeHeight, baseRadius)) {
            return false;
        }

        // Generate the trunk (hollow cylinder)
        generateHollowTrunk(level, pos, treeHeight, baseRadius, hollowRadius, random);

        // Generate the canopy
        generateCanopy(level, pos.above(treeHeight), baseRadius + 2, random);

        // Maybe place a chest in the hollow
        if (random.nextFloat() < 0.15F) { // 15% chance for chest
            placeChest(level, pos.above(1), random);
        }

        return true;
    }

    private boolean isValidLocation(WorldGenLevel level, BlockPos pos, int height, int radius) {
        // Check if there's solid ground at the base
        if (!level.getBlockState(pos.below()).isSolid()) {
            return false;
        }

        // Check if there's enough space above
        for (int y = 0; y <= height + 4; y++) {
            for (int x = -radius - 1; x <= radius + 1; x++) {
                for (int z = -radius - 1; z <= radius + 1; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    // Allow some blocks in the outer edges for more natural placement
                    double distance = Math.sqrt(x * x + z * z);
                    if (distance <= radius && y < height && level.getBlockState(checkPos).isSolid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void generateHollowTrunk(WorldGenLevel level, BlockPos basePos, int height, int outerRadius, int innerRadius, RandomSource random) {
        BlockState logState = Blocks.OAK_LOG.defaultBlockState();
        
        for (int y = 0; y < height; y++) {
            for (int x = -outerRadius; x <= outerRadius; x++) {
                for (int z = -outerRadius; z <= outerRadius; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    
                    BlockPos pos = basePos.offset(x, y, z);
                    
                    // Create hollow effect - only place blocks in the "wall" area
                    if (distance <= outerRadius && distance > innerRadius) {
                        // Add some randomness to make it look more organic
                        if (distance > outerRadius - 0.5 || random.nextFloat() < 0.9F) {
                            level.setBlock(pos, logState, 2);
                        }
                    }
                    // Create some inner wall details for the hollow part
                    else if (distance <= innerRadius && y > 0 && random.nextFloat() < 0.1F) {
                        level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private void generateCanopy(WorldGenLevel level, BlockPos centerPos, int radius, RandomSource random) {
        BlockState leavesState = Blocks.OAK_LEAVES.defaultBlockState();
        
        // Generate a sphere-like canopy
        for (int y = -2; y <= 3; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + z * z + y * y * 0.7); // Flatten sphere slightly
                    
                    BlockPos pos = centerPos.offset(x, y, z);
                    
                    // Create organic leaf shape
                    if (distance <= radius - Math.abs(y) * 0.3 && random.nextFloat() < 0.8F) {
                        // Higher chance for leaves closer to center
                        float leafProbability = Math.max(0.3F, 1.0F - (float)(distance / radius));
                        if (random.nextFloat() < leafProbability) {
                            level.setBlock(pos, leavesState, 2);
                        }
                    }
                }
            }
        }
    }

    private void placeChest(WorldGenLevel level, BlockPos pos, RandomSource random) {
        // Find a good spot for the chest inside the hollow
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos chestPos = pos.offset(x, 0, z);
                
                // Make sure there's solid ground below and air above
                if (level.getBlockState(chestPos.below()).isSolid() && 
                    level.getBlockState(chestPos).isAir() && 
                    level.getBlockState(chestPos.above()).isAir()) {
                    
                    BlockState chestState = Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                    
                    level.setBlock(chestPos, chestState, 2);
                    
                    // Set chest loot table (use buried treasure loot for forest-themed items)
                    if (level.getBlockEntity(chestPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                        chest.setLootTable(BuiltInLootTables.BURIED_TREASURE, random.nextLong());
                    }
                    
                    return; // Only place one chest
                }
            }
        }
    }
}
package grill24.potionsplus.blockentity;

import grill24.potionsplus.block.VersatilePlantBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MysticalGardenBlockEntity extends BlockEntity {
    private static final int EFFECT_RADIUS = 5;
    private static final int MAX_CHARGE = 1000;
    private static final int CHARGE_DECAY_RATE = 1;
    private static final int GROWTH_BOOST_COST = 10;

    private int charge = 0;
    private Optional<Potion> storedPotion = Optional.empty();
    private int tickCount = 0;

    public MysticalGardenBlockEntity(BlockPos pos, BlockState blockState) {
        super(Blocks.MYSTICAL_GARDEN_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MysticalGardenBlockEntity blockEntity) {
        blockEntity.tick(level, pos);
    }

    private void tick(Level level, BlockPos pos) {
        tickCount++;
        
        if (!level.isClientSide) {
            // Slowly decay charge over time
            if (charge > 0 && tickCount % 20 == 0) {
                charge = Math.max(0, charge - CHARGE_DECAY_RATE);
                setChanged();
            }

            // Enhance nearby plants less frequently
            if (charge > 0 && tickCount % 120 == 0) {
                enhanceNearbyPlants((ServerLevel) level, pos);
            }
        } else {
            // Client-side particle effects - always spawn some, more when charged
            spawnParticles(level, pos);
        }
    }

    private void enhanceNearbyPlants(ServerLevel level, BlockPos gardenPos) {
        int enhancementsThisTick = 0;
        final int maxEnhancementsPerTick = 1; // Only enhance 1 plant per cycle
        
        for (BlockPos pos : BlockPos.betweenClosed(
                gardenPos.offset(-EFFECT_RADIUS, -2, -EFFECT_RADIUS),
                gardenPos.offset(EFFECT_RADIUS, 2, EFFECT_RADIUS))) {
            
            if (pos.equals(gardenPos)) continue;
            if (charge < GROWTH_BOOST_COST) break;
            if (enhancementsThisTick >= maxEnhancementsPerTick) break;

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            // Enhance VersatilePlantBlocks
            if (block instanceof VersatilePlantBlock) {
                if (enhanceVersatilePlant(level, pos, state)) {
                    enhancementsThisTick++;
                }
            }
            // Enhance any bonemealable blocks
            else if (block instanceof BonemealableBlock bonemealable) {
                if (bonemealable.isValidBonemealTarget(level, pos, state) && 
                    level.random.nextFloat() < 0.3f) {
                    if (enhanceBonemealableBlock(level, pos, state, bonemealable)) {
                        enhancementsThisTick++;
                    }
                }
            }
        }
    }

    private boolean enhanceVersatilePlant(ServerLevel level, BlockPos pos, BlockState state) {
        // VersatilePlantBlocks can grow longer segments
        if (state.getBlock() instanceof VersatilePlantBlock versatilePlant) {
            // Try to grow the plant
            if (level.random.nextFloat() < 0.4f) {
                // Find the tip of the plant and try to grow it
                BlockPos tipPos = versatilePlant.getTailPos(level, pos, state);
                BlockState tipState = level.getBlockState(tipPos);
                
                if (tipState.is(versatilePlant)) {
                    // Try to place a new segment
                    var facing = tipState.getValue(VersatilePlantBlock.FACING);
                    BlockPos newPos = tipPos.relative(facing);
                    
                    if (level.isEmptyBlock(newPos) && 
                        tipState.getValue(VersatilePlantBlock.SEGMENT) < versatilePlant.getMaxSegmentIndex()) {
                        
                        BlockState newState = tipState.setValue(VersatilePlantBlock.SEGMENT, 
                            tipState.getValue(VersatilePlantBlock.SEGMENT) + 1);
                        
                        if (newState.canSurvive(level, newPos)) {
                            level.setBlockAndUpdate(newPos, newState);
                            charge -= GROWTH_BOOST_COST;
                            spawnGrowthEffect(level, newPos);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean enhanceBonemealableBlock(ServerLevel level, BlockPos pos, BlockState state, BonemealableBlock bonemealable) {
        if (bonemealable.isBonemealSuccess(level, level.random, pos, state)) {
            bonemealable.performBonemeal(level, level.random, pos, state);
            charge -= GROWTH_BOOST_COST;
            spawnGrowthEffect(level, pos);
            return true;
        }
        return false;
    }

    private void spawnGrowthEffect(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.5f, 1.2f);
        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + level.random.nextGaussian() * 0.3;
            double y = pos.getY() + 0.5 + level.random.nextGaussian() * 0.3;
            double z = pos.getZ() + 0.5 + level.random.nextGaussian() * 0.3;
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, 
                x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private void spawnParticles(Level level, BlockPos pos) {
        // Always spawn some ambient particles, but more when charged
        boolean isCharged = charge > 0;
        float baseChance = isCharged ? 0.6f : 0.2f; // More particles when charged
        int particleCount = isCharged ? 2 : 1; // Spawn more particles when charged
        
        if (tickCount % 15 == 0 && level.random.nextFloat() < baseChance) {
            for (int i = 0; i < particleCount; i++) {
                double x = pos.getX() + 0.2 + level.random.nextFloat() * 0.6;
                double y = pos.getY() + 0.8 + level.random.nextFloat() * 0.4;
                double z = pos.getZ() + 0.2 + level.random.nextFloat() * 0.6;
                
                level.addParticle(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    x, y, z, 
                    (level.random.nextFloat() - 0.5) * 0.1,
                    0.05,
                    (level.random.nextFloat() - 0.5) * 0.1);
            }
        }
    }

    public void performRandomTick(ServerLevel level, BlockPos pos, RandomSource random) {
        if (charge > 0 && random.nextFloat() < 0.1f) {
            // Occasionally play ambient sounds
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.3f, 1.5f);
        }
    }

    public boolean chargeWithPotion(PotionContents potionContents) {
        if (charge < MAX_CHARGE) {
            Optional<Holder<Potion>> potion = potionContents.potion();
            if (potion.isPresent()) {
                this.storedPotion = potion.map(Holder::value);
                this.charge = Math.min(MAX_CHARGE, charge + 150); // Add 150 charge
                setChanged();
                return true;
            }
        }
        return false;
    }

    public int getRedstoneSignal() {
        return charge > 0 ? Math.max(1, charge * 15 / MAX_CHARGE) : 0;
    }

    public int getCharge() {
        return charge;
    }

    public Optional<Potion> getStoredPotion() {
        return storedPotion;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Charge", charge);
        if (storedPotion.isPresent()) {
            // Simplified storage - we'll just store the charge
            tag.putBoolean("HasStoredPotion", true);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        charge = tag.getInt("Charge").orElse(0);
        if (tag.getBoolean("HasStoredPotion").orElse(false)) {
            // Simplified for now - just indicate we had a potion
            storedPotion = Optional.empty(); // Will be restored on next potion use
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Charge", charge);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        charge = tag.getInt("Charge").orElse(0);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
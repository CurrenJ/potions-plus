package grill24.potionsplus.block;

import com.mojang.serialization.DataResult;
import grill24.potionsplus.blockentity.InventoryBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.utility.Genotype;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class GeneticCropBlockEntity extends InventoryBlockEntity {
    private Genotype genotype;
    private Genotype pollinatorGenotype;

    private long placementTime;
    private long lastPollinationTime;

    public GeneticCropBlockEntity(BlockPos pos, BlockState blockState) {
        super(Blocks.GENETIC_CROP_BLOCK_ENTITY.value(), pos, blockState);

        this.genotype = new Genotype();
        this.pollinatorGenotype = null;

        this.lastPollinationTime = -1;
        this.placementTime = -1;
    }

    public int determineAge(int ticksPerAge) {
        if (this.level == null) {
            return 0;
        }

        if (this.placementTime == -1) {
            long currentAge = this.getBlockState().getValue(GeneticCropBlock.AGE);
            this.placementTime = this.level.getGameTime() - (currentAge * ticksPerAge);
            this.setChanged();
        }

        long ageTicks = this.level.getGameTime() - this.placementTime;
        int age = (int) (ageTicks / (float) ticksPerAge);

        return age;
    }

    public GeneticCropBlock.HarvestState getHarvestState(int minHarvestAge, int ticksPerAge, int growthTicks) {
        if (this.getBlockState().getValue(GeneticCropBlock.HARVESTABLE) == GeneticCropBlock.HarvestState.MATURE) {
            return GeneticCropBlock.HarvestState.MATURE;
        }

        if (this.level == null || this.lastPollinationTime < 0) {
            return this.getBlockState().getValue(GeneticCropBlock.HARVESTABLE);
        }

        long timeSincePollination = this.level.getGameTime() - this.lastPollinationTime;
        boolean mature = timeSincePollination >= growthTicks;

        boolean isMinMatureAge = determineAge(ticksPerAge) >= minHarvestAge;

        if (mature && isMinMatureAge) {
            return GeneticCropBlock.HarvestState.MATURE;
        } else if (isPollinated()) {
            return GeneticCropBlock.HarvestState.POLLINATED;
        } else {
            return GeneticCropBlock.HarvestState.IMMATURE;
        }
    }

    public void onPlace(ItemStack stack) {
        if (this.level == null) {
            return;
        }

        this.placementTime = this.level.getGameTime();
        this.genotype = stack.getOrDefault(DataComponents.GENETIC_DATA, new Genotype());
        this.lastPollinationTime = -1; // Reset pollination time on placement
        this.pollinatorGenotype = null; // Reset pollinator genotype on placement
        this.setChanged();
    }

    public boolean onPollinate(Genotype pollinatorGenotype) {
        if (pollinatorGenotype == null) {
            throw new IllegalArgumentException("Pollinator genotype cannot be null");
        }

        if (this.level == null || this.pollinatorGenotype != null || this.getBlockState().getValue(GeneticCropBlock.HARVESTABLE) != GeneticCropBlock.HarvestState.IMMATURE) {
            return false;
        }

        if (this.level != null && !this.level.isClientSide) {
            this.lastPollinationTime = this.level.getGameTime();
            this.pollinatorGenotype = pollinatorGenotype;
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(GeneticCropBlock.HARVESTABLE, GeneticCropBlock.HarvestState.POLLINATED), 2);
            this.setChanged();
        }
        return true;
    }

    public void onHarvest() {
        if (this.level == null) {
            return;
        }

        // Reset pollination state on harvest
        this.lastPollinationTime = this.level.getGameTime();
        BlockState newState = this.getBlockState().setValue(GeneticCropBlock.HARVESTABLE, GeneticCropBlock.HarvestState.POLLINATED);
        this.level.setBlock(this.getBlockPos(), newState, 2);
        this.setChanged();
    }

    public boolean isPollinated() {
        return this.pollinatorGenotype != null;
    }

    public ItemStack getOffspring() {
        Optional<GeneticCropBlock> cropBlock = this.getCropBlock();
        if (cropBlock.isEmpty()) {
            PotionsPlus.LOGGER.warn("GeneticCropBlockEntity: No crop block found for producing offspring.");
            return ItemStack.EMPTY;
        }

        if (this.level == null || this.pollinatorGenotype == null || this.genotype == null) {
            PotionsPlus.LOGGER.warn("GeneticCropBlockEntity: Cannot produce offspring, missing genotype or pollinator genotype.");

            Genotype genotype = new Genotype();
            if (this.genotype != null) {
                genotype = this.genotype;
            }

            ItemStack stack = new ItemStack(cropBlock.get());
            stack.set(DataComponents.GENETIC_DATA, genotype);
            return stack;
        }

        Genotype offspringGenotype = Genotype.crossover(this.genotype, this.pollinatorGenotype);
        offspringGenotype = Genotype.tryUniformMutate(offspringGenotype, 0.02F);

        ItemStack offspringStack = new ItemStack(cropBlock.get().getCropItem());
        offspringStack.set(DataComponents.GENETIC_DATA, offspringGenotype);

        if (offspringStack.getItem() instanceof GeneticCropItem geneticCropItem) {
            offspringStack = geneticCropItem.onGeneticDataChanged(offspringStack);
        }

        return offspringStack;
    }

    public Optional<GeneticCropBlock> getCropBlock() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof GeneticCropBlock) {
            return Optional.of((GeneticCropBlock) state.getBlock());
        }
        return Optional.empty();
    }

    public Genotype getGenotype() {
        return genotype;
    }

    @Override
    protected int getSlots() {
        return 0;
    }

    @Override
    public void readPacketNbt(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        Tag genotypeTag = tag.get("genotype");
        if (genotypeTag != null) {
            DataResult<Genotype> genotypeResult = Genotype.CODEC.parse(NbtOps.INSTANCE, genotypeTag);
            genotypeResult.result().ifPresentOrElse(g -> this.genotype = g, () -> this.genotype = new Genotype());
        }

        Tag pollinatorTag = tag.get("pollinatorGenotype");
        if (pollinatorTag != null) {
            DataResult<Genotype> pollinatorResult = Genotype.CODEC.parse(NbtOps.INSTANCE, pollinatorTag);
            pollinatorResult.result().ifPresentOrElse(g -> this.pollinatorGenotype = g, () -> this.pollinatorGenotype = null);
        }

        this.placementTime = tag.getLongOr("placementTime", -1L);
        this.lastPollinationTime = tag.getLongOr("lastPollinationTime", -1L);
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        if (genotype != null) {
            DataResult<Tag> genotypeTag = Genotype.CODEC.encodeStart(NbtOps.INSTANCE, this.genotype);
            genotypeTag.result().ifPresent(g -> tag.put("genotype", g));
        }

        if (this.pollinatorGenotype != null) {
            DataResult<Tag> pollinatorTag = Genotype.CODEC.encodeStart(NbtOps.INSTANCE, this.pollinatorGenotype);
            pollinatorTag.result().ifPresent(g -> tag.put("pollinatorGenotype", g));
        }

        tag.putLong("placementTime", this.placementTime);
        tag.putLong("lastPollinationTime", this.lastPollinationTime);
    }

    /**
     * Only for use during world-gen, not for player interaction.
     * @param genotype the genotype of the crop
     */
    public void setGenotype(Genotype genotype) {
        this.genotype = genotype;
        this.setChanged();
    }

    /**
     * Only for use during world-gen, not for player interaction.
     * @param pollinatorGenotype the genotype of the pollinator that pollinated this crop
     */
    public void setPollinatorGenotype(Genotype pollinatorGenotype) {
        this.pollinatorGenotype = pollinatorGenotype;
        this.setChanged();
    }
}

package grill24.potionsplus.utility;

import java.util.Arrays;

/**
 * A flexible Genotype class that supports any number of chromosomes (8-bit unsigned integers).
 * This is an enhanced version of the original Genotype class that removes the 8-chromosome limitation.
 * 
 * Key improvements over the original Genotype:
 * - Supports unlimited number of chromosomes (not just 8)
 * - Maintains all genetic operations (crossover, mutation, uniform mutation)
 * - Preserves binary string reducibility for genetic algorithms
 * - Backward compatible interface with original Genotype methods
 * - Enhanced crossover and mutation operations that work across different genome lengths
 */
public class NewGenotype {
    private byte[] chromosomes;

    /*
     * TODO: Uncomment when Minecraft dependencies are available in build environment
     * 
     * import com.mojang.serialization.Codec;
     * import io.netty.buffer.ByteBuf;
     * import net.minecraft.network.codec.ByteBufCodecs;
     * import net.minecraft.network.codec.StreamCodec;
     * 
     * public static final Codec<NewGenotype> CODEC = Codec.BYTE_BUFFER.xmap(
     *         buffer -> {
     *             byte[] bytes = new byte[buffer.remaining()];
     *             buffer.get(bytes);
     *             return new NewGenotype(bytes);
     *         },
     *         genotype -> java.nio.ByteBuffer.wrap(genotype.chromosomes)
     * );
     * 
     * public static final StreamCodec<ByteBuf, NewGenotype> STREAM_CODEC = StreamCodec.composite(
     *         ByteBufCodecs.BYTE_ARRAY,
     *         NewGenotype::getChromosomes,
     *         NewGenotype::new
     * );
     */

    /**
     * Creates a new Genotype with the specified chromosome values.
     *
     * @param chromosomes Array of chromosome values (0-255)
     */
    public NewGenotype(int... chromosomes) {
        if (chromosomes == null || chromosomes.length == 0) {
            this.chromosomes = new byte[0];
        } else {
            this.chromosomes = new byte[chromosomes.length];
            for (int i = 0; i < chromosomes.length; i++) {
                if (chromosomes[i] < 0 || chromosomes[i] > 255) {
                    throw new IllegalArgumentException("Chromosome values must be between 0 and 255");
                }
                this.chromosomes[i] = (byte) chromosomes[i];
            }
        }
    }

    /**
     * Creates a new Genotype with the specified byte array.
     *
     * @param chromosomes Array of chromosome values as bytes
     */
    public NewGenotype(byte... chromosomes) {
        this.chromosomes = chromosomes != null ? Arrays.copyOf(chromosomes, chromosomes.length) : new byte[0];
    }

    /**
     * Creates a new empty Genotype.
     */
    public NewGenotype() {
        this.chromosomes = new byte[0];
    }

    /**
     * Gets a copy of the chromosome array.
     *
     * @return Copy of the chromosome array
     */
    public byte[] getChromosomes() {
        return Arrays.copyOf(chromosomes, chromosomes.length);
    }

    /**
     * Gets the chromosome array as integers (unsigned bytes).
     * This method maintains compatibility with the original Genotype interface.
     *
     * @return Array of chromosome values as integers
     */
    public int[] getGenotypeAsIntArray() {
        return getChromosomesAsIntArray();
    }

    /**
     * Gets the chromosomes as a byte array.
     * This method maintains compatibility with the original Genotype interface.
     *
     * @return Copy of the chromosome array
     */
    public byte[] getGenotypeAsBytes() {
        return getChromosomes();
    }

    /**
     * Sets the genotype from a byte array.
     * This method maintains compatibility with the original Genotype interface.
     *
     * @param genotype New chromosome values
     */
    public void setGenotype(byte[] genotype) {
        setChromosomes(genotype);
    }

    /**
     * Gets the chromosome array as integers (unsigned bytes).
     *
     * @return Array of chromosome values as integers
     */
    public int[] getChromosomesAsIntArray() {
        int[] intArray = new int[chromosomes.length];
        for (int i = 0; i < chromosomes.length; i++) {
            intArray[i] = chromosomes[i] & 0xFF; // Convert byte to unsigned int
        }
        return intArray;
    }

    /**
     * Sets the chromosomes from a byte array.
     *
     * @param chromosomes New chromosome values
     */
    public void setChromosomes(byte[] chromosomes) {
        this.chromosomes = chromosomes != null ? Arrays.copyOf(chromosomes, chromosomes.length) : new byte[0];
    }

    /**
     * Gets the number of chromosomes in this genotype.
     *
     * @return Number of chromosomes
     */
    public int getChromosomeCount() {
        return chromosomes.length;
    }

    /**
     * Gets the value of a specific chromosome.
     *
     * @param index Chromosome index
     * @return Chromosome value (0-255)
     */
    public int getChromosome(int index) {
        if (index < 0 || index >= chromosomes.length) {
            throw new IndexOutOfBoundsException("Chromosome index " + index + " out of bounds for length " + chromosomes.length);
        }
        return chromosomes[index] & 0xFF;
    }

    /**
     * Sets the value of a specific chromosome.
     *
     * @param index Chromosome index
     * @param value Chromosome value (0-255)
     */
    public void setChromosome(int index, int value) {
        if (index < 0 || index >= chromosomes.length) {
            throw new IndexOutOfBoundsException("Chromosome index " + index + " out of bounds for length " + chromosomes.length);
        }
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Chromosome value must be between 0 and 255");
        }
        chromosomes[index] = (byte) value;
    }

    /**
     * Converts the genotype to a binary string representation.
     *
     * @return Binary string representation
     */
    public String toBinaryString() {
        StringBuilder sb = new StringBuilder();
        for (byte chromosome : chromosomes) {
            for (int bit = 7; bit >= 0; bit--) {
                sb.append((chromosome >> bit) & 1);
            }
        }
        return sb.toString();
    }

    /**
     * Gets the total number of bits in this genotype.
     *
     * @return Total number of bits
     */
    public int getBitCount() {
        return chromosomes.length * 8;
    }

    /**
     * Performs single-point crossover between two genotypes.
     * The crossover point is aligned to byte boundaries.
     *
     * @param g1 First parent genotype
     * @param g2 Second parent genotype
     * @return New genotype created from crossover
     */
    public static NewGenotype crossover(NewGenotype g1, NewGenotype g2) {
        if (g1.chromosomes.length == 0 && g2.chromosomes.length == 0) {
            return new NewGenotype();
        }

        int maxLength = Math.max(g1.chromosomes.length, g2.chromosomes.length);
        byte[] result = new byte[maxLength];

        // Choose crossover point (chromosome boundary)
        int crossoverPoint = (int) (Math.random() * maxLength);
        boolean flip = Math.random() < 0.5;

        for (int i = 0; i < maxLength; i++) {
            byte val1 = i < g1.chromosomes.length ? g1.chromosomes[i] : 0;
            byte val2 = i < g2.chromosomes.length ? g2.chromosomes[i] : 0;

            if (flip) {
                result[i] = (i < crossoverPoint) ? val2 : val1;
            } else {
                result[i] = (i < crossoverPoint) ? val1 : val2;
            }
        }

        return new NewGenotype(result);
    }

    /**
     * Performs uniform mutation on the genotype.
     *
     * @param genotype      The genotype to mutate
     * @param mutationRate  Probability of mutation per bit (0.0 to 1.0)
     * @return New mutated genotype
     */
    public static NewGenotype tryUniformMutate(NewGenotype genotype, float mutationRate) {
        if (genotype.chromosomes.length == 0) {
            return new NewGenotype();
        }

        byte[] mutated = Arrays.copyOf(genotype.chromosomes, genotype.chromosomes.length);
        int totalBits = genotype.getBitCount();

        for (int bitIndex = 0; bitIndex < totalBits; bitIndex++) {
            if (Math.random() < mutationRate) {
                int byteIndex = bitIndex / 8;
                int bitPosition = bitIndex % 8;
                mutated[byteIndex] ^= (1 << bitPosition); // Flip the bit
            }
        }

        return new NewGenotype(mutated);
    }

    /**
     * Mutates a random bit in the genotype.
     *
     * @param genotype The genotype to mutate
     * @return New mutated genotype
     */
    public static NewGenotype mutateRandom(NewGenotype genotype) {
        if (genotype.chromosomes.length == 0) {
            return new NewGenotype();
        }

        int totalBits = genotype.getBitCount();
        int mutationPoint = (int) (Math.random() * totalBits);

        return mutate(genotype, mutationPoint);
    }

    /**
     * Mutates a specific bit in the genotype.
     *
     * @param genotype     The genotype to mutate
     * @param mutationPoint The bit position to mutate
     * @return New mutated genotype
     */
    public static NewGenotype mutate(NewGenotype genotype, int mutationPoint) {
        if (genotype.chromosomes.length == 0) {
            return new NewGenotype();
        }

        int totalBits = genotype.getBitCount();
        if (mutationPoint < 0 || mutationPoint >= totalBits) {
            throw new IllegalArgumentException("Mutation point must be between 0 and " + (totalBits - 1));
        }

        byte[] mutated = Arrays.copyOf(genotype.chromosomes, genotype.chromosomes.length);
        int byteIndex = mutationPoint / 8;
        int bitPosition = mutationPoint % 8;
        mutated[byteIndex] ^= (1 << bitPosition); // Flip the bit

        return new NewGenotype(mutated);
    }

    @Override
    public String toString() {
        return "NewGenotype{" + Arrays.toString(getChromosomesAsIntArray()) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewGenotype that)) return false;
        return Arrays.equals(chromosomes, that.chromosomes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chromosomes);
    }
}
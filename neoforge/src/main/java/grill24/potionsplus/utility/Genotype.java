package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlus;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Genotype {
    private long genotype;

    public static final Codec<Genotype> CODEC = Codec.LONG.xmap(
            Genotype::new,
            Genotype::getGenotype
    );

    public static final StreamCodec<ByteBuf, Genotype> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG,
            Genotype::getGenotype,
            Genotype::new
    );

    public Genotype(int... genotype) {
        if (genotype == null || genotype.length == 0) {
            this.genotype = 0L; // Default to 0 for an empty genotype
        } else {
            // Convert int array to byte array
            byte[] byteGenotype = new byte[genotype.length];
            for (int i = 0; i < genotype.length; i++) {
                if (genotype[i] < 0 || genotype[i] > 255) {
                    throw new IllegalArgumentException("Genotype values must be between 0 and 255");
                }
                byteGenotype[i] = (byte) genotype[i];
            }

            this.genotype = encodeGenotype(byteGenotype);
        }
    }

    public Genotype(byte... genotype) {
        this.genotype = encodeGenotype(genotype);
    }

    public Genotype(long genotype) {
        this.genotype = genotype;
    }

    @SubscribeEvent
    public static void onModInit(FMLCommonSetupEvent event) {
        Genotype g1 = new Genotype(1, 2, 3, 5, 8, 13, 21, 34);
        PotionsPlus.LOGGER.info(g1.toString());
        Genotype g2 = new Genotype(2, 3, 5, 8, 13, 21, 34, 55);
        PotionsPlus.LOGGER.info(g2.toString());

        Genotype crossoverGenotype = Genotype.crossover(g1, g2);
        PotionsPlus.LOGGER.info("Crossover Genotype: " + crossoverGenotype);

        Genotype mutatedGenotype = Genotype.tryUniformMutate(crossoverGenotype, (float) 4 / BITS_IN_GENOTYPE);
        PotionsPlus.LOGGER.info("Mutated Genotype: " + mutatedGenotype);
    }

    /**
     * Encodes a genotype represented as a byte array into a long value.
     * Each byte (chromosome) in the genotype is encoded as 8 bits of the 128-bit long value (max 16 chromosomes).
     *
     * @param genotype The byte array representing the genotype.
     * @return A long value representing the encoded genotype.
     */
    public static long encodeGenotype(byte[] genotype) {
        if (genotype == null || genotype.length == 0) {
            return 0L; // Return 0 for an empty genotype
        }
        if (genotype.length > 16) {
            throw new IllegalArgumentException("Genotype length exceeds maximum of 16 chromosomes");
        }

        long encoded = 0;
        for (int i = 0; i < genotype.length; i++) {
            encoded |= ((long) genotype[i] & 0xFF) << (8 * i);
        }
        return encoded;
    }

    public void setGenotype(byte[] genotype) {
        this.genotype = encodeGenotype(genotype);
    }

    /**
     * Returns the encoded genotype as a long value.
     *
     * @return The long value representing the encoded genotype.
     */
    public long getGenotype() {
        return genotype;
    }

    public byte[] getGenotypeAsBytes() {
        return decodeGenotype(genotype);
    }

    public int[] getGenotypeAsIntArray() {
        byte[] bytes = decodeGenotype(genotype);
        int[] intArray = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            intArray[i] = bytes[i] & 0xFF; // Convert byte to unsigned int
        }
        return intArray;
    }

    /**
     * Decodes a long value back into a genotype represented as a byte array.
     * Each 8 bits of the long value corresponds to one chromosome in the genotype.
     *
     * @param encoded The long value representing the encoded genotype.
     * @return A byte array representing the decoded genotype.
     */
    public static byte[] decodeGenotype(long encoded) {
        byte[] chromosomes = new byte[MAX_CHROMOSOMES]; // Maximum 8 chromosomes
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = (byte) ((encoded >> (BYTE_SIZE * i)) & 0xFF);
        }
        return chromosomes;
    }

    private static final int BITS_IN_GENOTYPE = 64; // 16 bytes * 8 bits
    private static final int BYTE_SIZE = 8; // Size of a byte in bits
    public static final int MAX_CHROMOSOMES = BITS_IN_GENOTYPE / BYTE_SIZE; // Maximum 16 chromosomes

    public static Genotype crossover(Genotype g1, Genotype g2) {
        long genotype1 = g1.getGenotype();
        long genotype2 = g2.getGenotype();

        // Perform k-point crossover by picking a random crossover point (some bit out of long's 64 bits) and swapping all bits after that point
        int crossoverPoint = (int) (Math.random() * BITS_IN_GENOTYPE);
        // Round to nearest byte boundary
        crossoverPoint = (crossoverPoint / BYTE_SIZE) * BYTE_SIZE; // Ensure crossover point is a multiple of 8

        boolean flip = Math.random() < 0.5; // Randomly decide whether to cross left or right
        long mask = (1L << crossoverPoint) - 1; // Create a mask for the crossover point
        if (flip) {
            mask = ~mask; // Invert the mask to cross left
        }
        long newGenotype = (genotype1 & mask) | (genotype2 & ~mask); // Combine the two genotypes at the crossover point

        return new Genotype(newGenotype);
    }

    public static Genotype mutateRandom(Genotype g) {
        // Randomly flip a bit in the genotype
        int mutationPoint = (int) (Math.random() * BITS_IN_GENOTYPE);

        return mutate(g, mutationPoint);
    }

    public static Genotype mutate(Genotype g, int mutationPoint) {
        long genotype = g.getGenotype();
        if (mutationPoint < 0 || mutationPoint >= BITS_IN_GENOTYPE) {
            throw new IllegalArgumentException("Mutation point must be between 0 and " + (BITS_IN_GENOTYPE - 1));
        }
        long mutationMask = 1L << mutationPoint; // Create a mask for the mutation point
        long mutatedGenotype = genotype ^ mutationMask; // Flip the bit at the mutation point

        return new Genotype(mutatedGenotype);
    }

    public static Genotype tryUniformMutate(Genotype g, float mutationRate) {
        long mutatedGenotype = g.getGenotype();

        for (int i = 0; i < BITS_IN_GENOTYPE; i++) {
            if (Math.random() < mutationRate) {
                mutatedGenotype = mutate(g, i).getGenotype();
            }
        }

        return new Genotype(mutatedGenotype);
    }

    @Override
    public String toString() {
        return "Genotype{" + java.util.Arrays.toString(getGenotypeAsIntArray()) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genotype genotype1)) return false;
        return genotype == genotype1.genotype;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(genotype);
    }
}

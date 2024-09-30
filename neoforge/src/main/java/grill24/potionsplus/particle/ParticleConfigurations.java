package grill24.potionsplus.particle;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.Particles;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

import static grill24.potionsplus.utility.Utility.mc;

public class ParticleConfigurations {
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration END_ROD_RAIN = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.END_STONE_BRICKS, 32, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.END_ROD_RAIN.value(), 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BLUE_FIREY =
            new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.SOUL_SAND, 40,
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.SOUL, 1),
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.WHITE_ASH, 3),
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.SOUL_FIRE_FLAME, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration RUNES = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.CRYING_OBSIDIAN, 30, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.ENCHANT, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration PORTAL = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.AMETHYST_BLOCK, 20, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.PORTAL, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BUBBLES = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.PRISMARINE, 12, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.BUBBLE_POP, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration WANDERING_HEARTS = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.FIRE_CORAL_BLOCK, 6, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.WANDERING_HEART.value(), 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration FIREY = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.MAGMA_BLOCK, 80,
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.CAMPFIRE_COSY_SMOKE, 1),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.ASH, 6),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.SMALL_FLAME, 2),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.FALLING_LAVA, 2));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration LAVA_GEYSER_PARTICLES = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.MAGMA_BLOCK, 80,
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.SMALL_FLAME, 2),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(ParticleTypes.SMOKE, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration MUSICAL = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.NOTE_BLOCK, 14, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.RANDOM_NOTE.value(), 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BLOOD = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.REDSTONE_BLOCK, 1, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.BLOOD_GOB.value(), 1));

    // Emitter for particles that are not particles themselves, but rather emitters themselves. This is used for particles that are not directly rendered, but rather spawn other particles.
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration END_ROD_RAIN_EMITTER = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.TARGET, 1, true, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.END_ROD_RAIN_EMITTER.value(), 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration FIREY_EMITTER = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.NETHERRACK, 1, true, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.FIREY_EMITTER.value(), 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration LUNAR_BERRY_BUSH_AMBIENT = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.MOSS_BLOCK, 1, true, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.LUNAR_BERRY_BUSH_AMBIENT.value(), 1));
}

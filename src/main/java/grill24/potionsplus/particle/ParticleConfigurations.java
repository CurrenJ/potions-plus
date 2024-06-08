package grill24.potionsplus.particle;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.Particles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleConfigurations {
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration END_ROD_RAIN = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.END_STONE_BRICKS, 32, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.END_ROD_RAIN, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BLUE_FIREY =
            new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.SOUL_SAND, 40,
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.SOUL, 1),
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.WHITE_ASH, 3),
                    new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.SOUL_FIRE_FLAME, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration RUNES = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.CRYING_OBSIDIAN, 30, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.ENCHANT, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration PORTAL = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.AMETHYST_BLOCK, 20, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.PORTAL, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BUBBLES = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.PRISMARINE, 12, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.BUBBLE_POP, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration WANDERING_HEARTS = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.FIRE_CORAL_BLOCK, 6, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.WANDERING_HEART, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration FIREY = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.MAGMA_BLOCK, 80,
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.CAMPFIRE_COSY_SMOKE, 1),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.ASH, 6),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.SMALL_FLAME, 2),
            new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Minecraft.FALLING_LAVA, 2));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration MUSICAL = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.NOTE_BLOCK, 14, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.RANDOM_NOTE, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration BLOOD = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.REDSTONE_BLOCK, 1, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.BLOOD_GOB, 1));

    // Emitter for particles that are not particles themselves, but rather emitters themselves. This is used for particles that are not directly rendered, but rather spawn other particles.
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration END_ROD_RAIN_EMITTER = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.TARGET, 1, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.END_ROD_RAIN_EMITTER, 1));
    public static final ParticleEmitterBlock.ParticleEmitterConfiguration FIREY_EMITTER = new ParticleEmitterBlock.ParticleEmitterConfiguration(Blocks.NETHERRACK, 1, new ParticleEmitterBlock.ParticleEmitterConfiguration.WeightedParticleType(Particles.FIREY_EMITTER, 1));

    private static RegistryObject<SimpleParticleType> getVanillaParticle(String name) {
        return RegistryObject.create(new ResourceLocation("minecraft", name), ForgeRegistries.PARTICLE_TYPES);
    }

    public static class Minecraft {
        private static final RegistryObject<SimpleParticleType> SOUL = getVanillaParticle("soul");
        private static final RegistryObject<SimpleParticleType> ENCHANT = getVanillaParticle("enchant");
        public static final RegistryObject<SimpleParticleType> PORTAL = getVanillaParticle("portal");
        public static final RegistryObject<SimpleParticleType> BUBBLE_POP = getVanillaParticle("bubble_pop");
        public static final RegistryObject<SimpleParticleType> SPLASH = getVanillaParticle("splash");
        public static final RegistryObject<SimpleParticleType> BUBBLE_COLUMN_UP = getVanillaParticle("bubble_column_up");
        public static final RegistryObject<SimpleParticleType> CAMPFIRE_COSY_SMOKE = getVanillaParticle("campfire_cosy_smoke");
        public static final RegistryObject<SimpleParticleType> ASH = getVanillaParticle("ash");
        private static final RegistryObject<SimpleParticleType> SMALL_FLAME = getVanillaParticle("small_flame");
        private static final RegistryObject<SimpleParticleType> FALLING_LAVA = getVanillaParticle("falling_lava");
        private static final RegistryObject<SimpleParticleType> WHITE_ASH = getVanillaParticle("white_ash");
        public static final RegistryObject<SimpleParticleType> SOUL_FIRE_FLAME = getVanillaParticle("soul_fire_flame");
    }
}

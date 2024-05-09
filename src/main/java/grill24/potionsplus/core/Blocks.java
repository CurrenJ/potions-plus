package grill24.potionsplus.core;

import grill24.potionsplus.block.BrewingCauldronBlock;
import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MOD_ID);
    public static final RegistryObject<Block> BREWING_CAULDRON = BLOCKS.register("brewing_cauldron", () ->
            new BrewingCauldronBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
    public static final RegistryObject<Block> PARTICLE_EMITTER = BLOCKS.register("particle_emitter", () ->
            new ParticleEmitterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
}

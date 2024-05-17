package grill24.potionsplus.core;

import grill24.potionsplus.block.BrewingCauldronBlock;
import grill24.potionsplus.block.HerbalistsLecternBlock;
import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntity;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MOD_ID);
    public static final RegistryObject<Block> BREWING_CAULDRON = BLOCKS.register("brewing_cauldron", () ->
            new BrewingCauldronBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
    public static final RegistryObject<Block> PARTICLE_EMITTER = BLOCKS.register("particle_emitter", () ->
            new ParticleEmitterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> HERBALISTS_LECTERN = BLOCKS.register("herbalists_lectern", () ->
            new HerbalistsLecternBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));


    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ModInfo.MOD_ID);

    public static final RegistryObject<BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_cauldron_block_entity", () -> BlockEntityType.Builder.of(BrewingCauldronBlockEntity::new, BREWING_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY = BLOCK_ENTITIES.register("herbalists_lectern_block_entity", () -> BlockEntityType.Builder.of(HerbalistsLecternBlockEntity::new, HERBALISTS_LECTERN.get()).build(null));

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                Optional<BrewingCauldronBlockEntity> brewingCauldron = world.getBlockEntity(pos, BREWING_CAULDRON_BLOCK_ENTITY.get());
                if (brewingCauldron.isPresent()) {
                    return brewingCauldron.get().getWaterColor(world, pos);
                }
            }
            // No block entity or world, just return the biome color. This can happen bc block entity creation is lazy and can be null up until first interaction with it.
            return BiomeColors.getAverageWaterColor(world, pos);
        }, BREWING_CAULDRON.get());
    }
}

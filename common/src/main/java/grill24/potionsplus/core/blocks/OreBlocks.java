package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.UraniumOreBlock;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class OreBlocks {
    public static Holder<Block> URANIUM_ORE, DEEPSLATE_URANIUM_ORE, URANIUM_BLOCK, URANIUM_GLASS;
    public static Holder<Block> REMNANT_DEBRIS, DEEPSLATE_REMNANT_DEBRIS;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_ore")
                .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(5, 10), prop))
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.STONE))
                .modelGenerator(h -> new grill24.potionsplus.utility.registration.block.UraniumOreBlockModelGenerator<>(h, ppId("block/uranium_ore")))
        ).getHolder();
        RegistrationUtility.registerBlockItem(URANIUM_ORE, registerItem);

        DEEPSLATE_URANIUM_ORE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_uranium_ore")
                .blockFactory(prop -> new UraniumOreBlock(UniformInt.of(5, 10), prop))
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE))
                .modelGenerator(h -> new grill24.potionsplus.utility.registration.block.UraniumOreBlockModelGenerator<>(h, ppId("block/deepslate_uranium_ore")))
        ).getHolder();
        RegistrationUtility.registerBlockItem(DEEPSLATE_URANIUM_ORE, registerItem);

        URANIUM_BLOCK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_block")
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))
        ).getHolder();
        RegistrationUtility.registerBlockItem(URANIUM_BLOCK, registerItem);

        URANIUM_GLASS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("uranium_glass")
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noOcclusion().strength(0.3F).sound(SoundType.GLASS))
        ).getHolder();
        RegistrationUtility.registerBlockItem(URANIUM_GLASS, registerItem);

        REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("remnant_debris")
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.STONE))
        ).getHolder();
        RegistrationUtility.registerBlockItem(REMNANT_DEBRIS, registerItem);

        DEEPSLATE_REMNANT_DEBRIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("deepslate_remnant_debris")
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE))
        ).getHolder();
        RegistrationUtility.registerBlockItem(DEEPSLATE_REMNANT_DEBRIS, registerItem);
    }
}

package grill24.potionsplus.core;

import grill24.potionsplus.item.WormrootItem;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MOD_ID);

    // ----- Block Items -----

    public static final RegistryObject<Item> BREWING_CAULDRON = ITEMS.register(Blocks.BREWING_CAULDRON.getId().getPath(), () -> new BlockItem(Blocks.BREWING_CAULDRON.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> PARTICLE_EMITTER = ITEMS.register(Blocks.PARTICLE_EMITTER.getId().getPath(), () -> new BlockItem(Blocks.PARTICLE_EMITTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> PRECISION_DISPENSER = ITEMS.register(Blocks.PRECISION_DISPENSER.getId().getPath(), () -> new BlockItem(Blocks.PRECISION_DISPENSER.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> HERBALISTS_LECTERN = ITEMS.register(Blocks.HERBALISTS_LECTERN.getId().getPath(), () -> new BlockItem(Blocks.HERBALISTS_LECTERN.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> SANGUINE_ALTAR = ITEMS.register(Blocks.SANGUINE_ALTAR.getId().getPath(), () -> new BlockItem(Blocks.SANGUINE_ALTAR.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> ABYSSAL_TROVE = ITEMS.register(Blocks.ABYSSAL_TROVE.getId().getPath(), () -> new BlockItem(Blocks.ABYSSAL_TROVE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> LUNAR_BERRY_BUSH = ITEMS.register(Blocks.LUNAR_BERRY_BUSH.getId().getPath(), () -> new BlockItem(Blocks.LUNAR_BERRY_BUSH.get(), new Item.Properties()));
    public static final RegistryObject<Item> LUNAR_BERRIES = ITEMS.register("lunar_berries", () -> new ItemNameBlockItem(Blocks.LUNAR_BERRY_BUSH.get(), (new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).food(Foods.SWEET_BERRIES)));

    public static final RegistryObject<Item> CLOTHESLINE = ITEMS.register(Blocks.CLOTHESLINE.getId().getPath(), () -> new BlockItem(Blocks.CLOTHESLINE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> IRON_OXIDE_DAISY = ITEMS.register(Blocks.IRON_OXIDE_DAISY.getId().getPath(), () -> new BlockItem(Blocks.IRON_OXIDE_DAISY.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> COPPER_CHRYSANTHEMUM = ITEMS.register(Blocks.COPPER_CHRYSANTHEMUM.getId().getPath(), () -> new BlockItem(Blocks.COPPER_CHRYSANTHEMUM.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> LAPIS_LILAC = ITEMS.register(Blocks.LAPIS_LILAC.getId().getPath(), () -> new BlockItem(Blocks.LAPIS_LILAC.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> DIAMOUR = ITEMS.register(Blocks.DIAMOUR.getId().getPath(), () -> new BlockItem(Blocks.DIAMOUR.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> GOLDEN_CUBENSIS = ITEMS.register(Blocks.GOLDEN_CUBENSIS.getId().getPath(), () -> new BlockItem(Blocks.GOLDEN_CUBENSIS.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> REDSTONE_ROSE = ITEMS.register(Blocks.REDSTONE_ROSE.getId().getPath(), () -> new BlockItem(Blocks.REDSTONE_ROSE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> BLACK_COALLA_LILY = ITEMS.register(Blocks.BLACK_COALLA_LILY.getId().getPath(), () -> new BlockItem(Blocks.BLACK_COALLA_LILY.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> DENSE_DIAMOND_ORE = ITEMS.register(Blocks.DENSE_DIAMOND_ORE.getId().getPath(), () -> new BlockItem(Blocks.DENSE_DIAMOND_ORE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> DEEPSLATE_DENSE_DIAMOND_ORE = ITEMS.register(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.getId().getPath(), () -> new BlockItem(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> COOBLESTONE = ITEMS.register("cooblestone", () -> new BlockItem(Blocks.COOBLESTONE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> UNSTABLE_BLOCK = ITEMS.register("unstable_block", () -> new BlockItem(Blocks.UNSTABLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> UNSTABLE_MOLTEN_DEEPSLATE = ITEMS.register("unstable_molten_deepslate", () -> new BlockItem(Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> UNSTABLE_DEEPSLATE = ITEMS.register("unstable_deepslate", () -> new BlockItem(Blocks.UNSTABLE_DEEPSLATE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> UNSTABLE_MOLTEN_BLACKSTONE = ITEMS.register("unstable_molten_blackstone", () -> new BlockItem(Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> UNSTABLE_BLACKSTONE = ITEMS.register("unstable_blackstone", () -> new BlockItem(Blocks.UNSTABLE_BLACKSTONE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> DECORATIVE_FIRE = ITEMS.register("decorative_fire", () -> new BlockItem(Blocks.DECORATIVE_FIRE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));


    public static final RegistryObject<Item> WREATH = ITEMS.register("wreath", () ->  new ArmorItem(ArmorMaterials.WREATH, EquipmentSlot.HEAD, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));

    // ----- Dynamically Rendered Display Items -----

    public static final RegistryObject<Item> POTION_EFFECT_ICON = ITEMS.register("potion_effect_icon", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> GENERIC_ICON = ITEMS.register("generic_icon", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final List<ResourceLocation> GENERIC_ICON_RESOURCE_LOCATIONS = new ArrayList<>() {{
        add(new ResourceLocation(ModInfo.MOD_ID, "amplifier_upgrade"));
        add(new ResourceLocation(ModInfo.MOD_ID, "duration_upgrade"));
        add(new ResourceLocation(ModInfo.MOD_ID, "i"));
        add(new ResourceLocation(ModInfo.MOD_ID, "ii"));
        add(new ResourceLocation(ModInfo.MOD_ID, "iii"));
        add(new ResourceLocation(ModInfo.MOD_ID, "iv"));
        add(new ResourceLocation(ModInfo.MOD_ID, "v"));
        add(new ResourceLocation(ModInfo.MOD_ID, "vi"));
        add(new ResourceLocation(ModInfo.MOD_ID, "vii"));
        add(new ResourceLocation(ModInfo.MOD_ID, "viii"));
        add(new ResourceLocation(ModInfo.MOD_ID, "arrow"));
        add(new ResourceLocation(ModInfo.MOD_ID, "unknown"));
        add(new ResourceLocation("sga_a"));
        add(new ResourceLocation("sga_b"));
        add(new ResourceLocation("sga_c"));
        add(new ResourceLocation("sga_d"));
    }};

    public static final String DYNAMIC_ICON_INDEX_PROPERTY_NAME = "dynamic_icon_index";

    // ----- Items -----

    public static final RegistryObject<Item> MOSS = ITEMS.register("moss", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> WORMROOT = ITEMS.register("wormroot", () -> new WormrootItem(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> ROTTEN_WORMROOT = ITEMS.register("rotten_wormroot", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Register item models
        event.enqueueWork(() -> {
            // Register item properties
            ClampedItemPropertyFunction clampedItemPropertyFunction =
                    (stack, world, entity, i) -> (float) (stack.getCount() - 1) / 64.0F + 0.01F;
            ResourceLocation propertyId = new ResourceLocation(ModInfo.MOD_ID, DYNAMIC_ICON_INDEX_PROPERTY_NAME);
            ItemProperties.register(POTION_EFFECT_ICON.get(), propertyId, clampedItemPropertyFunction);
            ItemProperties.register(GENERIC_ICON.get(), propertyId, clampedItemPropertyFunction);

            System.out.println(ItemProperties.PROPERTIES);
        });
    }
}

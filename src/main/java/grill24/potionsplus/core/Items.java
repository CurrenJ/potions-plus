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

import static grill24.potionsplus.core.CreativeModeTabs.POTIONS_PLUS_TAB;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MOD_ID);

    // ----- Items -----

    public static final RegistryObject<Item> WREATH = ITEMS.register("wreath", () ->  new ArmorItem(ArmorMaterials.WREATH, EquipmentSlot.HEAD, properties()));

    public static final RegistryObject<Item> LUNAR_BERRIES = ITEMS.register("lunar_berries", () -> new ItemNameBlockItem(Blocks.LUNAR_BERRY_BUSH.get(), properties().food(Foods.SWEET_BERRIES)));
    public static final RegistryObject<Item> MOSS = ITEMS.register("moss", () -> new Item(properties()));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(properties()));
    public static final RegistryObject<Item> WORMROOT = ITEMS.register("wormroot", () -> new WormrootItem(properties()));
    public static final RegistryObject<Item> ROTTEN_WORMROOT = ITEMS.register("rotten_wormroot", () -> new Item(properties()));


    // ----- Dynamically Rendered Display Items -----

    public static final RegistryObject<Item> POTION_EFFECT_ICON = ITEMS.register("potion_effect_icon", () -> new Item(properties()));

    public static final RegistryObject<Item> GENERIC_ICON = ITEMS.register("generic_icon", () -> new Item(properties()));
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

    public static Item.Properties properties() {
        return new Item.Properties().tab(POTIONS_PLUS_TAB);
    }
}

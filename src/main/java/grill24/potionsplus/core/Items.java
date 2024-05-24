package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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
    public static final RegistryObject<Item> BREWING_CAULDRON = ITEMS.register(Blocks.BREWING_CAULDRON.getId().getPath(), () -> new BlockItem(Blocks.BREWING_CAULDRON.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> PARTICLE_EMITTER = ITEMS.register(Blocks.PARTICLE_EMITTER.getId().getPath(), () -> new BlockItem(Blocks.PARTICLE_EMITTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> PRECISION_DISPENSER = ITEMS.register(Blocks.PRECISION_DISPENSER.getId().getPath(), () -> new BlockItem(Blocks.PRECISION_DISPENSER.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

    public static final RegistryObject<Item> HERBALISTS_LECTERN = ITEMS.register(Blocks.HERBALISTS_LECTERN.getId().getPath(), () -> new BlockItem(Blocks.HERBALISTS_LECTERN.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> SANGUINE_ALTAR = ITEMS.register(Blocks.SANGUINE_ALTAR.getId().getPath(), () -> new BlockItem(Blocks.SANGUINE_ALTAR.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> ABYSSAL_TROVE = ITEMS.register(Blocks.ABYSSAL_TROVE.getId().getPath(), () -> new BlockItem(Blocks.ABYSSAL_TROVE.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));

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
}

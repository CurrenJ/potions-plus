package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MOD_ID);
    public static final RegistryObject<Item> BREWING_CAULDRON = ITEMS.register(Blocks.BREWING_CAULDRON.getId().getPath(), () -> new BlockItem(Blocks.BREWING_CAULDRON.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    public static final RegistryObject<Item> PARTICLE_EMITTER = ITEMS.register(Blocks.PARTICLE_EMITTER.getId().getPath(), () -> new BlockItem(Blocks.PARTICLE_EMITTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
}

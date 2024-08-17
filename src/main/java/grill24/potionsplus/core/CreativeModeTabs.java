package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CreativeModeTabs {
    public static final CreativeModeTab POTIONS_PLUS_TAB = new CreativeModeTabSupplier(new ResourceLocation(ModInfo.MOD_ID, "potions_plus"),
            () -> new ItemStack(Items.LUNAR_BERRIES.get()));

    public static class CreativeModeTabSupplier extends CreativeModeTab {
        private final Supplier<ItemStack> icon;

        public CreativeModeTabSupplier(ResourceLocation id, Supplier<ItemStack> icon) {
            super(String.format("%s.%s", id.getNamespace(), id.getPath()));
            this.icon = icon;
        }

        @Override
        public ItemStack makeIcon() {
            return icon.get();
        }
    }
}

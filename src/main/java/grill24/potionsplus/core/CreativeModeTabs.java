package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModInfo.MOD_ID);

    public static ResourceKey<CreativeModeTab> POTIONS_PLUS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            new ResourceLocation(ModInfo.MOD_ID, "potions_plus"));
    public static final RegistryObject<CreativeModeTab> POTIONS_PLUS_TAB = CREATIVE_MODE_TABS.register(POTIONS_PLUS_TAB_KEY.location().getPath(), () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.potionsplus").withStyle(style -> style.withColor(ChatFormatting.WHITE)))
                    .icon(() -> new ItemStack(Items.LUNAR_BERRIES.get()))
                    .withSearchBar()
                    .build());

    @SubscribeEvent
    public static void registerTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == POTIONS_PLUS_TAB_KEY) {
            for (RegistryObject<Item> item : Items.ITEMS.getEntries()) {
                event.accept(item);
            }

            for (RegistryObject<Block> block : Blocks.BLOCKS.getEntries()) {
                event.accept(block);
            }
        }
    }
}

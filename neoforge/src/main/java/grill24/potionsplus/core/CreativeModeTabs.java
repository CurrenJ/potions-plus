package grill24.potionsplus.core;

import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModInfo.MOD_ID);

    public static ResourceKey<CreativeModeTab> POTIONS_PLUS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            ppId("potions_plus"));
    public static final Holder<CreativeModeTab> POTIONS_PLUS_TAB = CREATIVE_MODE_TABS.register(POTIONS_PLUS_TAB_KEY.location().getPath(), () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.potionsplus").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)))
                    .icon(() -> new ItemStack(BrewingItems.LUNAR_BERRIES.value()))
                    .withSearchBar()
                    .build());

    @SubscribeEvent
    public static void registerTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == POTIONS_PLUS_TAB_KEY) {
            for (Holder<Item> item : Items.ITEMS.getEntries()) {
                event.accept(item.value());
            }
        }
    }
}

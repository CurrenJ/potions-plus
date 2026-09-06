package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModInfo.MOD_ID);

    public static ResourceKey<CreativeModeTab> POTIONS_PLUS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ppId("potions_plus"));
    public static final ForgeHolder<CreativeModeTab> POTIONS_PLUS_TAB = ForgeHolder.of(CREATIVE_MODE_TABS.register(POTIONS_PLUS_TAB_KEY.identifier().getPath(), () ->
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
                    .title(Component.translatable("itemGroup.potionsplus").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)))
                    .icon(() -> new ItemStack(BrewingItems.LUNAR_BERRIES.value()))
                    // Vanilla has no BuildCreativeModeTabContentsEvent; enumerate every potionsplus item
                    // at display-build time (all items are registered by then). No in-tab search bar
                    // (NeoForge-only withSearchBar()); global search still finds items.
                    .displayItems((params, output) -> BuiltInRegistries.ITEM.entrySet().stream()
                            .filter(entry -> entry.getKey().identifier().getNamespace().equals(ModInfo.MOD_ID))
                            .forEach(entry -> output.accept(entry.getValue())))
                    .build()));
}

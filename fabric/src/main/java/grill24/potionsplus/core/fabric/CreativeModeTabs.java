package grill24.potionsplus.core.fabric;

import grill24.potionsplus.core.items.BrewingItems;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static grill24.potionsplus.utility.Utility.ppId;

public class CreativeModeTabs {
    public static ResourceKey<CreativeModeTab> POTIONS_PLUS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ppId("potions_plus"));
    public static final Holder<CreativeModeTab> POTIONS_PLUS_TAB = FabricRegistration.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, POTIONS_PLUS_TAB_KEY.identifier().getPath(), () ->
                    CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
                            .title(Component.translatable("itemGroup.potionsplus").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)))
                            .icon(() -> new ItemStack(BrewingItems.LUNAR_BERRIES.value()))
                            // Vanilla has no BuildCreativeModeTabContentsEvent; enumerate every potionsplus
                            // item at display-build time (all items are registered by then).
                            .displayItems((params, output) -> BuiltInRegistries.ITEM.entrySet().stream()
                                    .filter(entry -> entry.getKey().identifier().getNamespace().equals(ModInfo.MOD_ID))
                                    .forEach(entry -> output.accept(entry.getValue())))
                            .build());

    public static void init() {
    }
}

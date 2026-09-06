package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
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
import net.minecraftforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModInfo.MOD_ID);
    public static ResourceKey<CreativeModeTab> POTIONS_PLUS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ppId("potions_plus"));
    public static final Holder<CreativeModeTab> POTIONS_PLUS_TAB = ForgeHolder.of(CREATIVE_MODE_TABS.register(
            POTIONS_PLUS_TAB_KEY.location().getPath(), () ->
                    // builder(Row.TOP, 4) is the vanilla surface; Forge 52.1.2 ALSO patches the no-arg
                    // builder() and withSearchBar() in (verified via javap on the srg-patched jar).
                    // builder(Row.TOP, 4) + withSearchBar() is the safe cross-patch union.
                    CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
                            .title(Component.translatable("itemGroup.potionsplus").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)))
                            .icon(() -> new ItemStack(BrewingItems.LUNAR_BERRIES.value()))
                            .withSearchBar()
                            // Forge has no BuildCreativeModeTabContentsEvent (NeoForge-only); the vanilla
                            // DisplayItemsFunction (invoked at tab-display time, after all items are
                            // registered) enumerates every potionsplus item instead.
                            .displayItems((params, output) -> BuiltInRegistries.ITEM.entrySet().stream()
                                    .filter(entry -> entry.getKey().location().getNamespace().equals(ModInfo.MOD_ID))
                                    .forEach(entry -> output.accept(entry.getValue())))
                            .build()));

    public static void init() {
    }
}

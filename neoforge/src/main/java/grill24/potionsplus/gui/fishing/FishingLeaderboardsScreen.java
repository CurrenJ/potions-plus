package grill24.potionsplus.gui.fishing;

import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.gui.*;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FishingLeaderboardsScreen extends PotionsPlusScreen<FishingLeaderboardsMenu> {
    private RenderableScreenElement root;

    public FishingLeaderboardsScreen(FishingLeaderboardsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected RenderableScreenElement getRootElement() {
        return root;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null || minecraft.player == null) {
            return;
        }


        TabsScreenElement<RenderableScreenElement> personalMetricTabsRenderer = createMetricTabs(this, menu, FishingLeaderboardScreenElement.Type.PERSONAL);
        TabsScreenElement<RenderableScreenElement> globalMetricTabsRenderer = createMetricTabs(this, menu, FishingLeaderboardScreenElement.Type.GLOBAL);

        TabsScreenElement<TabsScreenElement<RenderableScreenElement>> scopeTabsRenderer = new TabsScreenElement<>(this, null, RenderableScreenElement.Settings.DEFAULT,
                TabsScreenElement.TabData.verticalListTab(this, DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.GLOBAL_TEX_LOC), 1F, 1.25F, globalMetricTabsRenderer),
                TabsScreenElement.TabData.verticalListTab(this, Utility.getPlayerHead(minecraft.player), 1F, 1.25F, personalMetricTabsRenderer));

        VerticalScrollListScreenElement<RenderableScreenElement> all = new VerticalScrollListScreenElement<>(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.XAlignment.CENTER, () -> true, scopeTabsRenderer);
        all.setAllowClicksOutsideBounds(true);

        this.root = new FullScreenDivScreenElement<>(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.CENTER, all);
    }

    private TabsScreenElement<RenderableScreenElement> createMetricTabs(Screen screen, FishingLeaderboardsMenu menu, FishingLeaderboardScreenElement.Type type) {
        return new TabsScreenElement<>(screen, null, RenderableScreenElement.Settings.DEFAULT,
                TabsScreenElement.TabData.verticalListTab(screen, DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.RULER_TEX_LOC), 0.85F, 1.15F,
                        new FishingLeaderboardScreenElement(screen, this.minecraft.player, type, FishingLeaderboardScreenElement.Metric.SIZE)),
                TabsScreenElement.TabData.verticalListTab(screen, DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.COUNT_TEX_LOC), 0.85F, 1.15F,
                        new FishingLeaderboardScreenElement(screen, this.minecraft.player, type, FishingLeaderboardScreenElement.Metric.COUNT)));
    }
}

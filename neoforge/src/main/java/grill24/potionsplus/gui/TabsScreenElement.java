package grill24.potionsplus.gui;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabsScreenElement<E extends RenderableScreenElement> extends VerticalListScreenElement<E> {
    public static class TabData {
        public final SelectableDivScreenElement icon;
        public final RenderableScreenElement content;

        public TabData(SelectableDivScreenElement icon, RenderableScreenElement content) {
            this.icon = icon;
            this.content = content;
        }
    }

    private HorizontalListScreenElement<RenderableScreenElement> tabIcons;
    private TabData selectedTab;
    private TabData lastSelectedTab;

    public TabsScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, TabData... tabs) {
        super(screen, settings, XAlignment.CENTER);

        this.tabIcons = new HorizontalListScreenElement<>(screen, settings, YAlignment.CENTER);
        this.selectedTab = null;
        this.lastSelectedTab = null;

        initializeElements(tabs);
    }

    public void initializeElements(TabData... tabs) {
        List<RenderableScreenElement> icons = new ArrayList<>();
        List<RenderableScreenElement> contents = new ArrayList<>();
        for (TabData tab : tabs) {
            SelectableDivScreenElement icon = tab.icon;
            icon.addClickListener((x, y, button) -> {
                this.setSelectedTab(tab);
            });
            icons.add(icon);

            RenderableScreenElement content = tab.content;
            content.hide(false);
            contents.add(tab.content);
        }

        HorizontalListScreenElement<RenderableScreenElement> tabIcons = new HorizontalListScreenElement<>(screen, Settings.DEFAULT, YAlignment.CENTER);
        tabIcons.setChildren(icons);
        this.tabIcons = tabIcons;

        List<RenderableScreenElement> children = new ArrayList<>();
        children.add(tabIcons);
        children.addAll(contents);
        setChildren((Collection<E>) children);
    }

    public void setSelectedTab(TabData tab) {
        this.lastSelectedTab = this.selectedTab;
        if (this.lastSelectedTab != null) {
            this.lastSelectedTab.icon.setSelected(false);
            this.lastSelectedTab.content.hide(false);
        }

        this.selectedTab = tab;
        if (this.selectedTab != null) {
            if (this.selectedTab == this.lastSelectedTab) {
                this.selectedTab = null;
            } else {
                this.selectedTab.icon.setSelected(true);
                this.selectedTab.content.show();
            }
        }
    }
}

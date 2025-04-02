package grill24.potionsplus.gui.skill;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.gui.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.List;

public class SkillsScreen extends PotionsPlusScreen<SkillsMenu> {
    private SkillTitleScreenElement skillTitleRenderer;
    private SkillIconsScreenElement skillsIconsRenderer;

    private TabsScreenElement tabsRenderer;
    private AbilitiesListScreenElement abilitiesRenderer;
    private MilestonesScreenElement milestoneRenderer;
    private SkillRewardsListScreenElement rewardsRenderer;

    private RenderableScreenElement allScreenElements;


    public SkillsScreen(SkillsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected RenderableScreenElement getRootElement() {
        return allScreenElements;
    }

    @Override
    protected void init() {
        super.init();

        // Safety check
        if (this.minecraft == null || this.minecraft.level == null) {
            return;
        }

        // Initialize render elements
        // Skill title
        this.skillTitleRenderer = new SkillTitleScreenElement(this);
        // Skills icons
        this.skillsIconsRenderer = new SkillIconsScreenElement(this, RenderableScreenElement.Settings.DEFAULT, itemDisplay -> {
            ResourceKey<ConfiguredSkill<?, ?>> key = itemDisplay == null ? null : itemDisplay.skill.getKey();
            this.skillTitleRenderer.setSelectedSkill(key);
            this.abilitiesRenderer.setSelectedSkill(key);
            this.milestoneRenderer.setSelectedSkill(key);
            this.rewardsRenderer.setSelectedSkill(key);

            if (key == null) {
                this.tabsRenderer.hide(false, false);
            } else {
                this.tabsRenderer.show(false);
            }
        });
        this.skillsIconsRenderer.setAllowClicksOutsideBounds(true);
        SkillIconsDivElement skillsIconsRendererDiv = new SkillIconsDivElement(this, RenderableScreenElement.Settings.DEFAULT.withAnimationSpeed(1F), this.skillsIconsRenderer);
        skillsIconsRendererDiv.setAllowClicksOutsideBounds(true);


        // Spacer
        final RenderableScreenElement spacer = new FixedSizeDivScreenElement<>(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.DEFAULT, null, 4, 4);
        final RenderableScreenElement spacer2 = new FixedSizeDivScreenElement<>(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.DEFAULT, null, 4, 4);
        final RenderableScreenElement spacer3 = new FixedSizeDivScreenElement<>(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.DEFAULT, null, 4, 4);
        // Abilities list
        this.abilitiesRenderer = new AbilitiesListScreenElement(this, RenderableScreenElement.Settings.DEFAULT);
        // Milestones
        this.milestoneRenderer = new MilestonesScreenElement(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.YAlignment.CENTER);
        // Rewards
        this.rewardsRenderer = new SkillRewardsListScreenElement(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.XAlignment.CENTER);
        // Put abilities and milestones in a tab

        this.tabsRenderer = new TabsScreenElement<>(this, null, RenderableScreenElement.Settings.DEFAULT,
                TabsScreenElement.TabData.verticalListTab(this, new ItemStack(Items.ENCHANTED_BOOK), 1F, 1.2F, spacer2, this.abilitiesRenderer, this.milestoneRenderer),
                TabsScreenElement.TabData.verticalListTab(this, new ItemStack(Items.GOLD_INGOT), 1F, 1.2F, spacer3, this.rewardsRenderer));
        this.tabsRenderer.hide(false, false);

        // Add all elements to a vertical list
        VerticalScrollListScreenElement<RenderableScreenElement> elementsList =
                new VerticalScrollListScreenElement<>(this,
                        RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.XAlignment.CENTER,
                        this.skillTitleRenderer, skillsIconsRendererDiv, spacer, tabsRenderer);
        elementsList.setAllowClicksOutsideBounds(true);


        this.allScreenElements = new FullScreenDivScreenElement<VerticalScrollListScreenElement<?>>(
                this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.CENTER, elementsList);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.skillsIconsRenderer.getChildren().isEmpty()) {
            renderHelpText(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderHelpText(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render help text
        Font font = this.getMinecraft().font;
        Component component = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_JOURNAL_NONE);
        List<Component> animatedComponents = ItemListenersGame.animateComponentTextStartTime(Collections.singletonList(Collections.singletonList(component)), screenOpenedTimestamp);

        int textWidth = font.width(component);
        int textHeight = font.lineHeight;
        int x = (int) (this.width / 2F - textWidth / 2F);
        int y = (int) (this.height / 2F - textHeight / 2F);

        if (!animatedComponents.isEmpty()) {
            graphics.drawString(font, animatedComponents.get(0), x, y, 0xFFFFFF);
        }
    }
}
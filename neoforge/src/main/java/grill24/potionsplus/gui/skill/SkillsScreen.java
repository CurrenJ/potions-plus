package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.gui.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;

import java.util.*;
import java.util.List;

public class SkillsScreen extends AbstractContainerScreen<SkillsMenu> {
    private SkillTitleScreenElement skillTitleRenderer;
    private SkillIconsScreenElement skillsIconsRenderer;
    private AbilitiesListScreenElement abilitiesRenderer;
    private RenderableScreenElement allText;


    private MilestonesScreenElement milestoneRenderer;

    private final float screenOpenedTimestamp;



    public SkillsScreen(SkillsMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.screenOpenedTimestamp = ClientTickHandler.total();
    }

    @Override
    protected void init() {
        super.init();

        // Safety check
        if (this.minecraft == null || this.minecraft.level == null) {
            return;
        }

        // Milestones
        this.milestoneRenderer = new MilestonesScreenElement(this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.YAlignment.CENTER);

        // Initialize render elements
        this.skillTitleRenderer = new SkillTitleScreenElement(this);
        this.abilitiesRenderer = new AbilitiesListScreenElement(this, RenderableScreenElement.Settings.DEFAULT);
        this.skillsIconsRenderer = new SkillIconsScreenElement(this, RenderableScreenElement.Settings.DEFAULT, itemDisplay -> {
            ResourceKey<ConfiguredSkill<?, ?>> key = itemDisplay == null ? null : itemDisplay.skill.getKey();
            this.skillTitleRenderer.setSelectedSkill(key);
            this.abilitiesRenderer.setSelectedSkill(key);
            this.milestoneRenderer.setSkill(key);
        });
        SkillIconsDivElement skillsIconsRendererDiv = new SkillIconsDivElement(this, this.skillsIconsRenderer);
        skillsIconsRendererDiv.setAllowClicksOutsideBounds(true);

        VerticalListScreenElement<RenderableScreenElement> elementsList =
                new VerticalListScreenElement<>(this,
                        RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.XAlignment.CENTER,
                        this.skillTitleRenderer, skillsIconsRendererDiv, this.abilitiesRenderer, this.milestoneRenderer);
        elementsList.setAllowClicksOutsideBounds(true);

        this.allText = new FullScreenDivScreenElement<VerticalListScreenElement<?>>(
                this, RenderableScreenElement.Settings.DEFAULT, RenderableScreenElement.Anchor.CENTER, elementsList);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background is typically rendered first
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Render things here before widgets (background textures)

        // Then the widgets if this is a direct child of the Screen
        super.render(graphics, mouseX, mouseY, partialTick);

        allText.tick(partialTick, mouseX, mouseY);
        allText.tryRender(graphics, partialTick, mouseX, mouseY);

        if (this.skillsIconsRenderer.getChildren().isEmpty()) {
            renderHelpText(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Don't.
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }

    private void renderHelpText(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render help text
        Font font = this.getMinecraft().font;
        Component component = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_JOURNAL_NONE);
        List<Component> animatedComponents = ItemListenersGame.animateComponentText(Collections.singletonList(Collections.singletonList(component)), screenOpenedTimestamp);

        int textWidth = font.width(component);
        int textHeight = font.lineHeight;
        int x = (int) (this.width / 2F - textWidth / 2F);
        int y = (int) (this.height / 2F - textHeight / 2F);

        if (!animatedComponents.isEmpty()) {
            graphics.drawString(font, animatedComponents.get(0), x, y, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        allText.click((int) mouseX, (int) mouseY);

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
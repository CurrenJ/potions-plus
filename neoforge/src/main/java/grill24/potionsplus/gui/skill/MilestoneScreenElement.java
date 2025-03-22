package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.SimpleTooltipScreenElement;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.skill.Milestone;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.extension.IClientAdvancementsExtension;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class MilestoneScreenElement extends ItemStackScreenElement {
    private Milestone milestone;
    private boolean isUnlocked = false;
    private int appearDelay;

    public MilestoneScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Milestone milestone, int appearDelay) {
        super(screen, parent, settings, null);

        this.appearDelay = appearDelay;
        setMilestone(milestone);
    }

    @Override
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if(this.isUnlocked) {
            super.render(graphics, partialTick, mouseX, mouseY);
        } else {
        // Render item silhouette
        graphics.setColor(0, 0, 0, 1);
        super.render(graphics, partialTick, mouseX, mouseY);
        graphics.setColor(1, 1, 1, 1);

        if (this.screen.getMinecraft().player == null) {
            return;
        }

        // Render question mark
        Rectangle2D bounds = getGlobalBounds();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, -100);
        ((IGuiGraphicsExtension) graphics).potions_plus$renderItem(
                Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.UNKNOWN_TEX_LOC),
                new Vector3f(0, 0, 0),
                (float) (bounds.getMinX() + bounds.getWidth() / 4F), // The render method we are calling here renders an item centered at the given position. We align to top-left because that's how the screen elements assume bounds are positioned.
                (float) (bounds.getMinY() + bounds.getHeight() / 4F),
                10,
                this.getCurrentScale() * 0.5F,
                Anchor.DEFAULT);
        graphics.pose().popPose();
        }
    }

    @Override
    public void onTick(float partialTick, int mouseX, int mouseY) {
        float animationProgress = (ClientTickHandler.total() - this.shownTimestamp - this.appearDelay) / 20F;
        float scale = this.shownTimestamp != -1 ? SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate(animationProgress) : 0;
        this.setCurrentScale(scale);

        if (!this.isUnlocked) {
            this.rotation += partialTick;
        }

        super.onTick(partialTick, mouseX, mouseY);
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
        show();

        Optional<DisplayInfo> displayInfo = tryGetAdvancementDisplayInfo(this.milestone.advancementId());
        if (displayInfo.isPresent()) {
            DisplayInfo display = displayInfo.get();
            ItemStack stack = display.getIcon();

            setItemStack(stack);
            this.isUnlocked = isAdvancementUnlocked(this.milestone.advancementId());
            this.tooltip = new SimpleTooltipScreenElement(this.screen, Settings.DEFAULT.withAnchor(Anchor.BOTTOM_LEFT).withAnimationSpeed(1.0F).withHiddenByDefault(true), this.isUnlocked ? Color.GREEN : Color.RED, display.getDescription());
        } else {
            hide(false);
        }
    }

    @Override
    public void show() {
        super.show();
        if (this.stack == null || this.stack.isEmpty()) {
            hide(false);
        }
    }

    private Optional<DisplayInfo> tryGetAdvancementDisplayInfo(ResourceLocation advancementId) {
        Optional<ClientAdvancements> advancements = getAdvancements();
        if (!advancements.isPresent()) {
            return Optional.empty();
        }

        AdvancementHolder holder = advancements.get().get(advancementId);
        if (holder != null) {
            Advancement advancement = holder.value();
            return advancement.display();
        }

        return Optional.empty();
    }

    private boolean isAdvancementUnlocked(ResourceLocation advancementId) {
        Optional<ClientAdvancements> advancements = getAdvancements();
        return advancements
                .map(clientAdvancements -> ((IClientAdvancementsExtension) clientAdvancements).potions_plus$getAdvancementProgress(advancementId)
                .map(AdvancementProgress::isDone)
                .orElse(false))
                .orElse(false);
    }

    private Optional<ClientAdvancements> getAdvancements() {
        ClientPacketListener connection = this.screen.getMinecraft().getConnection();
        if (connection == null) {
            return Optional.empty();
        }

        return Optional.of(connection.getAdvancements());
    }

    @Override
    protected void onMouseEnter(int x, int y) {
        if (this.tooltip != null) {
            this.tooltip.show();
        }
    }

    @Override
    protected void onMouseExit(int x, int y) {
        if (this.tooltip != null) {
            this.tooltip.hide(false);
        }
    }
}

package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.skill.Milestone;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.IClientAdvancementsMixin;
import grill24.potionsplus.utility.IGuiGraphicsMixin;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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

        // Render question mark
        if (this.screen.getMinecraft().player == null) {
            return;
        }
        Rectangle2D bounds = getGlobalBounds();
        ((IGuiGraphicsMixin) graphics).potions_plus$renderItem(
                new ItemStack(Items.GENERIC_ICON, 12),
                new Vector3f(0, 0, 0),
                (float) (bounds.getMinX() + bounds.getWidth() / 4F), // The render method we are calling here renders an item centered at the given position. We align to top-left because that's how the screen elements assume bounds are positioned.
                (float) (bounds.getMinY() + bounds.getHeight() / 4F),
                10,
                this.scale * 0.5F,
                Anchor.DEFAULT);
        }
    }

    @Override
    public void onTick(float partialTick) {
        float animationProgress = (ClientTickHandler.total() - this.shownTimestamp - this.appearDelay) / 20F;
        float scale = this.shownTimestamp != -1 ? SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate(animationProgress) : 0;
        this.setScale(scale);

        if (!this.isUnlocked) {
            this.rotation += partialTick;
        }

        super.onTick(partialTick);
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
        show();

        ClientPacketListener connection = this.screen.getMinecraft().getConnection();
        if (connection == null) {
            return;
        }

        ClientAdvancements advancements = connection.getAdvancements();
        AdvancementHolder holder = advancements.get(this.milestone.advancementId());
        if (holder != null) {
            Advancement advancement = holder.value();
            Optional<DisplayInfo> displayInfo = advancement.display();
            if (displayInfo.isPresent()) {
                DisplayInfo display = displayInfo.get();
                ItemStack stack = display.getIcon();

                setItemStack(stack);
                this.isUnlocked = ((IClientAdvancementsMixin) advancements).potions_plus$getAdvancementProgress(this.milestone.advancementId()).map(AdvancementProgress::isDone).orElse(false);
            }
        } else {
            hide();
        }
    }
}

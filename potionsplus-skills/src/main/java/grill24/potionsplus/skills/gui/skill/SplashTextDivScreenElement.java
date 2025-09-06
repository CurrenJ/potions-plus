package grill24.potionsplus.gui.skill;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.gui.DivScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SplashTextDivScreenElement extends DivScreenElement<RenderableScreenElement> {
    protected Component component;

    public SplashTextDivScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Anchor childAlignment, RenderableScreenElement child, Component component) {
        super(screen, parent, settings, childAlignment, child);

        this.component = component;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.render(graphics, partialTick, mouseX, mouseY);

        if (this.component != null) {
            IGuiGraphicsExtension graphicsExtension = (IGuiGraphicsExtension) graphics;
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 100); // Ensure text is rendered above other elements
            graphicsExtension.potions_plus$drawString(this.screen.getFont(), this.component, (float) getGlobalBounds().getMinX(), (float) getGlobalBounds().getMinY(), 0xFFFFFF);
            graphics.pose().popPose();
        }
    }
}

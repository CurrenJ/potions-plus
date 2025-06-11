package grill24.potionsplus.mixin;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements IGuiGraphicsExtension {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract void flush();

    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;


    @Shadow public abstract int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow);

    @Shadow public abstract int drawString(Font p_282636_, FormattedCharSequence p_281596_, float p_281586_, float p_282816_, int p_281743_, boolean p_282394_);

    @Shadow @Final private ItemStackRenderState scratchItemStackRenderState;
    private static final float PIX = 16;

    @Override
    public void potions_plus$renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor, int seed, float guiOffset) {
        if (!stack.isEmpty()) {
            this.minecraft
                    .getItemModelResolver()
                    .updateForTopItem(this.scratchItemStackRenderState, stack, ItemDisplayContext.GUI, level, entity, seed);
            this.pose.pushPose();
            this.pose.translate(x, y, 150 + guiOffset);

            float actualScale = PIX * scale;

            // Align the item's origin to the anchor point
            float xOffset = switch (anchor.xAlignment()) {
                case LEFT -> actualScale / 2F;
                case CENTER -> 0;
                case RIGHT -> -actualScale / 2F;
            };
            float yOffset = switch (anchor.yAlignment()) {
                case TOP -> actualScale / 2F;
                case CENTER -> 0;
                case BOTTOM -> -actualScale / 2F;
            };
            this.pose.translate(xOffset, yOffset, 0);

            this.pose.mulPose(RUtil.rotate(rotation));

            try {
                this.pose.scale(actualScale, -actualScale, actualScale);
                boolean flag = !this.scratchItemStackRenderState.usesBlockLight();
                if (flag) {
                    this.flush();
                    Lighting.setupForFlatItems();
                }

                this.scratchItemStackRenderState.render(this.pose, this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
                this.flush();
                if (flag) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                throw new ReportedException(crashreport);
            }

            this.pose.popPose();
        }
    }

    @Override
    public void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor) {
        this.potions_plus$renderItem(this.minecraft.player, this.minecraft.level, stack, rotation, x, y, scale, anchor, 0, 0);
    }

    @Override
    public void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float zOffset, float scale, RenderableScreenElement.Anchor anchor) {
        this.potions_plus$renderItem(this.minecraft.player, this.minecraft.level, stack, rotation, x, y, scale, anchor, 0, zOffset);
    }

    @Override
    public void potions_plus$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color) {
        Matrix4f matrix4f = this.pose.last().pose();
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        Vector2f[] points = {
                new Vector2f(minX, minY),
                new Vector2f(minX, maxY),
                new Vector2f(maxX, maxY),
                new Vector2f(maxX, minY)
        };
        RUtil.rotatePointsAround(points, origin, rotationDegrees);

        VertexConsumer vertexconsumer = this.bufferSource.getBuffer(renderType);
        for (Vector2f point : points) {
            vertexconsumer.addVertex(matrix4f, point.x(), point.y(), (float) z).setColor(color);
        }
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color) {
        this.potions_plus$fill(RenderType.gui(), minX, minY, maxX, maxY, origin, rotationDegrees, z, color);
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, float rotationDegrees, int z, int color) {
        Vector2f center = new Vector2f(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2);
        this.potions_plus$fill(minX, minY, maxX, maxY, center, rotationDegrees, z, color);
    }

    @Override
    public void potions_plus$fill(float minX, float minY, float maxX, float maxY, int color) {
        this.potions_plus$fill(minX, minY, maxX, maxY, 0, 0, color);
    }

    @Override
    public int potions_plus$drawString(Font font, Component text, float x, float y, int color) {
        return this.potions_plus$drawString(font, text, x, y, color, true);
    }

    @Override
    public int potions_plus$drawString(Font font, Component text, float x, float y, int color, boolean dropShadow) {
        return this.drawString(font, text.getVisualOrderText(), x, y, color, dropShadow);
    }
}

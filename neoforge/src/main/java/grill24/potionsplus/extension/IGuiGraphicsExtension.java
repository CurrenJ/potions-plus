package grill24.potionsplus.extension;

import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface IGuiGraphicsExtension {
    void potions_plus$renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor, int seed, float guiOffset);
    void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor);
    void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float zOffset, float scale, RenderableScreenElement.Anchor anchor);

    void potions_plus$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);
    void potions_plus$fill(float minX, float minY, float maxX, float maxY, float rotationDegrees, int z, int color);
    void potions_plus$fill(float minX, float minY, float maxX, float maxY, Vector2f origin, float rotationDegrees, int z, int color);
    void potions_plus$fill(float minX, float minY, float maxX, float maxY, int color);

    int potions_plus$drawString(Font font, Component text, float x, float y, int color);
    int potions_plus$drawString(Font font, Component text, float x, float y, int color, boolean dropShadow);
}

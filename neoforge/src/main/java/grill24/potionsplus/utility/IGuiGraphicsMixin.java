package grill24.potionsplus.utility;

import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface IGuiGraphicsMixin {
    void potions_plus$renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, Vector3f rotation, float x, float y, float zOffset, float scale, RenderableScreenElement.Anchor anchor, int seed, int guiOffset);
    void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float scale, RenderableScreenElement.Anchor anchor);
    void potions_plus$renderItem(ItemStack stack, Vector3f rotation, float x, float y, float zOffset, float scale, RenderableScreenElement.Anchor anchor);
}

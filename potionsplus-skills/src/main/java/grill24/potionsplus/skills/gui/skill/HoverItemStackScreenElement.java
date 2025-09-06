package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HoverItemStackScreenElement extends ItemStackScreenElement {
    private float defaultScale;
    private float hoverScale;

    public HoverItemStackScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, ItemStack stack, float defaultScale, float hoverScale) {
        super(screen, parent, settings, stack);

        this.defaultScale = defaultScale;
        this.hoverScale = hoverScale;
    }

    @Override
    public void onTick(float partialTick, int mouseX, int mouseY) {
        super.onTick(partialTick, mouseX, mouseY);

        float scale = RUtil.lerp(
                getCurrentScale(),
                isHovering() ? hoverScale : defaultScale,
                partialTick * this.settings.animationSpeed());
        if (Math.abs(scale) - 1 < 0.01F) {
            scale = 1F;
        }
        setCurrentScale(scale);
    }
}

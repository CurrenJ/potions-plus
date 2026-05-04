package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Environment(EnvType.CLIENT)
public class AbyssalTroveRenderState extends BlockEntityRenderState {
    public float currentDisplayRotation = 0f;
    public final Map<Integer, List<RenderedItem>> renderedItemTiers = new TreeMap<>();

    public static class RenderedItem {
        public final ItemStackRenderState icon = new ItemStackRenderState();
        public final List<ItemStackRenderState> subIcons = new ArrayList<>();
        public final Vector3d position;
        public float scale;
        public float subIconScale;
        public boolean isBlockItem;

        public RenderedItem(Vector3d position, float scale, float subIconScale, boolean isBlockItem) {
            this.position = position;
            this.scale = scale;
            this.subIconScale = subIconScale;
            this.isBlockItem = isBlockItem;
        }
    }
}

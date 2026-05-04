package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClotheslineRenderState extends BlockEntityRenderState {
    public boolean isLeftEnd = false;

    // Rope endpoints (world-space, used for submitCustomGeometry translate)
    public Vec3 leftVec = Vec3.ZERO;
    public Vec3 rightVec = Vec3.ZERO;
    public int blockLightStart = 0;
    public int blockLightEnd = 0;
    public int skyLightStart = 0;
    public int skyLightEnd = 0;

    // Fence post positions relative to this block entity
    public BlockPos leftRelative = BlockPos.ZERO;
    public BlockPos rightRelative = BlockPos.ZERO;
    public boolean hasFencePost = false;
    public final BlockModelRenderState fencePostModel = new BlockModelRenderState();
    public int fenceLightCoords = 15728880;

    // Hanging items
    public final List<HangingItem> hangingItems = new ArrayList<>();

    public static class HangingItem {
        public final ItemStackRenderState state = new ItemStackRenderState();
        public Vector3f position = new Vector3f();
        public Quaternionf orientation = new Quaternionf();
        public float swingAmplitude = 0f;
        public int slot = 0;
        public int lightCoords = 0;
    }

    // Clothesline facing direction
    public Direction facing = Direction.NORTH;
}

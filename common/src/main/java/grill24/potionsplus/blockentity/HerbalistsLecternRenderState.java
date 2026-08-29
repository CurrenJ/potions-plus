package grill24.potionsplus.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class HerbalistsLecternRenderState extends BlockEntityRenderState {
    public boolean hasInputItem = false;
    public final ItemStackRenderState inputItem = new ItemStackRenderState();

    // ISingleStackDisplayer snapshot for input item animation
    public int timeItemPlaced = 0;
    public org.joml.Vector3d startAnimationWorldPos = new org.joml.Vector3d();
    public org.joml.Vector3d restingPosition = new org.joml.Vector3d();
    public Vector3f restingRotation = new Vector3f();
    public int inputAnimationDuration = 0;

    public final List<IconEntry> icons = new ArrayList<>();
    public final ItemStackRenderState centerDisplayStack = new ItemStackRenderState();
    public boolean hasCenterDisplay = false;
    public Vector3f playerRelativePosition = new Vector3f();
    public Quaternionf numeralsRotation = new Quaternionf().identity();
    public float lerpFactor = 0f;

    public record IconEntry(ItemStackRenderState icon, List<ItemStackRenderState> subIcons) {}
}

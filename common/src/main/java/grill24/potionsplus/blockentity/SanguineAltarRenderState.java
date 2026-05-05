package grill24.potionsplus.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class SanguineAltarRenderState extends BlockEntityRenderState {
    public SanguineAltarBlockEntity.State altarState = SanguineAltarBlockEntity.State.IDLE;

    public final ItemStackRenderState inputItem = new ItemStackRenderState();
    public boolean hasInputItem = false;

    // ISingleStackDisplayer snapshot
    public int timeItemPlaced = 0;
    public Vector3d startAnimationWorldPos = new Vector3d();
    public Vector3d restingPosition = new Vector3d();
    public Vector3f restingRotation = new Vector3f();
    public int inputAnimationDuration = 0;

    // Converting / converted animation data
    public int nextSpinTickDelay = 0;
    public int nextSpinTotalRevolutions = 0;
    public float nextSpinHertz = 0f;
    public float healthDrainProgress = 0f;

    // Rune items (resolved model states)
    public final ItemStackRenderState[] runeItems = new ItemStackRenderState[4];

    public SanguineAltarRenderState() {
        for (int i = 0; i < runeItems.length; i++) {
            runeItems[i] = new ItemStackRenderState();
        }
    }
}

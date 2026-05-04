package grill24.potionsplus.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PotionBeaconRenderState extends BlockEntityRenderState {
    public boolean hasInputItem = false;
    public final ItemStackRenderState inputItem = new ItemStackRenderState();
    public int timeItemPlaced = 0;
    public Vector3d startAnimationWorldPos = new Vector3d();
    public Vector3d restingPosition = new Vector3d();
    public Vector3f restingRotation = new Vector3f();
    public int inputAnimationDuration = 0;

    public boolean isLit = false;
    public float innerBlockShownTimestamp = 0f;
    public float effectDurationWhenShown = 0f;

    public final List<ParticleEntry> particles = new ArrayList<>();

    public static class ParticleEntry {
        public Vector3d position;
        public Vector3d velocity;
        public Vector3f rotation;
        public Vector3f rotationalVelocity;
        public float age;
        public float lifetime;
        public float scale;

        public ParticleEntry(PotionBeaconBlockEntity.RendererData.ItemParticle src) {
            this.position = new Vector3d(src.position);
            this.velocity = new Vector3d(src.velocity);
            this.rotation = new Vector3f(src.rotation);
            this.rotationalVelocity = new Vector3f(src.rotationalVelocity);
            this.age = src.age;
            this.lifetime = src.lifetime;
            this.scale = src.scale;
        }
    }
}

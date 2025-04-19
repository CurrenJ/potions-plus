package grill24.potionsplus.item;

import net.minecraft.world.item.Item;
import org.joml.Vector3f;

public class PotionsPlusFishItem extends Item {
    public enum FishTankRenderType {
        DEFAULT_FISH(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, 45F), true),
        NO_ROTATION(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, 0F), true),
        UPRIGHT_FISH(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, -45F), true),
        GROUND_ANCHORED_NO_ROTATION(new Vector3f(0.5F, 2 / 16F, 0.5F), new Vector3f(0F, 0F, 0F), false);

        private final Vector3f position;
        private final Vector3f rotationOffsetDegrees;
        private final boolean bobY;
        FishTankRenderType(Vector3f position, Vector3f rotationOffsetDegrees, boolean bobY) {
            this.position = position;
            this.rotationOffsetDegrees = rotationOffsetDegrees;
            this.bobY = bobY;
        }

        public Vector3f getPosition() {
            return position;
        }

        public Vector3f getRotationOffsetDegrees() {
            return rotationOffsetDegrees;
        }

        public boolean shouldBobY() {
            return bobY;
        }
    }

    private final FishTankRenderType renderType;

    public PotionsPlusFishItem(Properties properties, FishTankRenderType renderType) {
        super(properties);
        this.renderType = renderType;
    }

    public PotionsPlusFishItem(Properties properties) {
        this(properties, FishTankRenderType.DEFAULT_FISH);
    }

    public FishTankRenderType getFishTankRenderType() {
        return renderType;
    }
}

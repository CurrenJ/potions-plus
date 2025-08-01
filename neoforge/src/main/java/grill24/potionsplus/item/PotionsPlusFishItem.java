package grill24.potionsplus.item;

import net.minecraft.world.item.Item;
import org.joml.Vector3f;

public class PotionsPlusFishItem extends Item {
    private static final float GROUND_Y = 2 / 16F;

    public enum FishTankRenderType {
        DEFAULT_FISH(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, 45F), true, true),
        NO_ROTATION(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, 0F), true, true),
        UPRIGHT_FISH(new Vector3f(0.5F, 0.5F, 0.5F), new Vector3f(0F, 0F, -45F), true, true),
        GROUND_ANCHORED_NO_ROTATION(new Vector3f(0.5F, GROUND_Y, 0.5F), new Vector3f(0F, 0F, 0F), false, true),
        LAY_FLAT_ON_GROUND(new Vector3f(0.5F, GROUND_Y, 0.5F), new Vector3f(90F, 0, 0F), false, false);

        private final Vector3f position;
        private final Vector3f rotationOffsetDegrees;
        private final boolean bobY;
        private final boolean enableWobbleAnimation;

        FishTankRenderType(Vector3f position, Vector3f rotationOffsetDegrees, boolean bobY, boolean enableWobbleAnimation) {
            this.position = position;
            this.rotationOffsetDegrees = rotationOffsetDegrees;
            this.bobY = bobY;
            this.enableWobbleAnimation = enableWobbleAnimation;
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

        public boolean isWobbleAnimationEnabled() {
            return enableWobbleAnimation;
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

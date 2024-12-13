package grill24.potionsplus.blockentity;

import org.joml.Vector3d;
import org.joml.Vector3f;

public interface ISingleStackDisplayer {
    int getTimeItemPlaced();

    Vector3d getStartAnimationWorldPos();

    Vector3d getRestingPosition();

    Vector3f getRestingRotation();

    int getInputAnimationDuration();
}
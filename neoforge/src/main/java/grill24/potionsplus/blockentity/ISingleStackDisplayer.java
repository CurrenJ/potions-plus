package grill24.potionsplus.blockentity;

import org.joml.Vector3d;
import org.joml.Vector3f;

public interface ISingleStackDisplayer {
    int getTimeItemPlaced();

    public Vector3d getStartAnimationWorldPos();

    public Vector3d getRestingPosition();

    public Vector3f getRestingRotation();

    public int getInputAnimationDuration();
}
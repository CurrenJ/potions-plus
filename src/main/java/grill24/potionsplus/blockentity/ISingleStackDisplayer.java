package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

public interface ISingleStackDisplayer {
    int getTimeItemPlaced();

    public Vector3d getStartAnimationWorldPos();

    public Vector3d getRestingPosition();

    public Vector3f getRestingRotation();

    public int getInputAnimationDuration();
}
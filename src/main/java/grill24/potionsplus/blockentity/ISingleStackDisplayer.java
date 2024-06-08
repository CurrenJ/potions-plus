package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3d;

public interface ISingleStackDisplayer {
    int getTimeItemPlaced();

    public Vector3d getStartAnimationWorldPos();

    public Vector3d getRestingPosition();

    public int getInputAnimationDuration();
}
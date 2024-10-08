package grill24.potionsplus.block;

import net.minecraft.util.StringRepresentable;

public enum ClotheslinePart implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    private ClotheslinePart(String p_61339_) {
        this.name = p_61339_;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}

package grill24.potionsplus.blockentity;

import net.minecraft.world.SimpleContainer;

public class PotionsPlusContainer extends SimpleContainer {
    private int maxStackSize = 0;

    public PotionsPlusContainer(int size, int maxStackSize) {
        super(size);
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }
}

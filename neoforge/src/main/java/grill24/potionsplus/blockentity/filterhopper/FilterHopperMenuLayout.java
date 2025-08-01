package grill24.potionsplus.blockentity.filterhopper;

public record FilterHopperMenuLayout(int playerInventoryTexX, int playerInventoryTexY,
                                     int hoppperFilterSlotsTexX, int hoppperFilterSlotsTexY,
                                     int maxFilterRowLength, int filterSlots,
                                     int hopperSlotsTexX, int hopperSlotsTexY,
                                     int hopperSlots,
                                     int upgradeSlotsX, int upgradeSlotsY,
                                     int maxUpgradeRowLength, int upgradeSlots,
                                     int upgradeSlotsPaddingBetweenRows, int upgradeSlotsPaddingBetweenColumns) {
    public int getTotalSlots() {
        return filterSlots + hopperSlots + upgradeSlots;
    }

    public static class Builder {
        private int playerInventoryTexX = -1;
        private int playerInventoryTexY = -1;
        private int hoppperFilterSlotsTexX = -1;
        private int hoppperFilterSlotsTexY = -1;
        private int maxFilterRowLength = -1;
        private int filterSlots = -1;
        private int hopperSlotsTexX = -1;
        private int hopperSlotsTexY = -1;
        private int hopperSlots = -1;
        private int upgradeSlotsX = -1;
        private int upgradeSlotsY = -1;
        private int maxUpgradeRowLength = -1;
        private int upgradeSlots = -1;
        private int upgradeSlotsPaddingBetweenRows = 0;
        private int upgradeSlotsPaddingBetweenColumns = 0;

        public Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder playerInv(int textureX, int textureY) {
            this.playerInventoryTexX = textureX;
            this.playerInventoryTexY = textureY;
            return this;
        }

        public Builder filterSlots(int textureX, int textureY, int maxRowLength, int numSlots) {
            this.hoppperFilterSlotsTexX = textureX;
            this.hoppperFilterSlotsTexY = textureY;
            this.maxFilterRowLength = maxRowLength;
            this.filterSlots = numSlots;
            return this;
        }

        public Builder hopperSlots(int textureX, int textureY, int numSlots) {
            this.hopperSlotsTexX = textureX;
            this.hopperSlotsTexY = textureY;
            this.hopperSlots = numSlots;
            return this;
        }

        public Builder upgradeSlots(int textureX, int textureY, int maxRowLength, int numSlots) {
            this.upgradeSlotsX = textureX;
            this.upgradeSlotsY = textureY;
            this.maxUpgradeRowLength = maxRowLength;
            this.upgradeSlots = numSlots;
            this.upgradeSlotsPaddingBetweenRows = 0;
            this.upgradeSlotsPaddingBetweenColumns = 0;
            return this;
        }

        public Builder upgradeSlots(int textureX, int textureY, int maxRowLength, int numSlots, int paddingBetweenRows, int paddingBetweenColumns) {
            this.upgradeSlotsX = textureX;
            this.upgradeSlotsY = textureY;
            this.maxUpgradeRowLength = maxRowLength;
            this.upgradeSlots = numSlots;
            this.upgradeSlotsPaddingBetweenRows = paddingBetweenRows;
            this.upgradeSlotsPaddingBetweenColumns = paddingBetweenColumns;
            return this;
        }

        public FilterHopperMenuLayout build() {
            return new FilterHopperMenuLayout(playerInventoryTexX, playerInventoryTexY,
                    hoppperFilterSlotsTexX, hoppperFilterSlotsTexY,
                    maxFilterRowLength, filterSlots,
                    hopperSlotsTexX, hopperSlotsTexY,
                    hopperSlots,
                    upgradeSlotsX, upgradeSlotsY,
                    maxUpgradeRowLength, upgradeSlots,
                    upgradeSlotsPaddingBetweenRows, upgradeSlotsPaddingBetweenColumns);
        }
    }
}

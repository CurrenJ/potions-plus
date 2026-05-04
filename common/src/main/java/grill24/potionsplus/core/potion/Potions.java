package grill24.potionsplus.core.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;

import java.util.ArrayList;
import java.util.List;

public class Potions {
    public static final List<PotionBuilder.PotionsPlusPotionGenerationData> ALL_POTION_GENERATION_DATA = new ArrayList<>();

    public static Holder<Potion> ANY_POTION;
    public static Holder<Potion> ANY_OTHER_POTION;

    public static PotionBuilder.PotionsPlusPotionGenerationData[] getAllPotionAmpDurMatrices() {
        return ALL_POTION_GENERATION_DATA.toArray(new PotionBuilder.PotionsPlusPotionGenerationData[0]);
    }
}

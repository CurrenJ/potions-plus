package grill24.potionsplus.data;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class PotionsPlusModelProvider extends ModelProvider {
    public PotionsPlusModelProvider(PackOutput output) {
        super(output, ModInfo.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {

    }
}

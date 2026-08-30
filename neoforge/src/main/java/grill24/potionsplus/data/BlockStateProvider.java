package grill24.potionsplus.data;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModInfo.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        RegistrationUtility.generateItemModels(ModInfo.MOD_ID, this);
    }

    private void registerItemFromParentWithTextureOverride(Item item, ResourceLocation parent, ResourceLocation textureOverride) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parent))
                .texture("layer0", textureOverride);
    }
}

package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ModInfo.MOD_ID);

    public static void init() {
        grill24.potionsplus.core.ArmorMaterials.init(ARMOR_MATERIALS::register);
    }
}

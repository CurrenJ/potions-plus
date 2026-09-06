package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.registries.DeferredRegister;

public class ArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ModInfo.MOD_ID);

    public static void init() {
        // Forge's RegistryObject does not implement Holder, so the common init(BiFunction) call
        // needs the ForgeHolder.of(...) lambda (a bare ARMOR_MATERIALS::register method reference
        // would not type-check, unlike NeoForge whose DeferredHolder implements Holder<T>).
        grill24.potionsplus.core.ArmorMaterials.init((name, supplier) -> ForgeHolder.of(ARMOR_MATERIALS.register(name, supplier)));
    }
}

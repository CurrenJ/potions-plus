package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class ArmorMaterials {
    public static DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ModInfo.MOD_ID);

    private static Map<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });
    private static List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(ppId("wreath")));

    public static Holder<ArmorMaterial> WREATH = ARMOR_MATERIALS.register("wreath", () -> new ArmorMaterial(
            HEALTH_FUNCTION_FOR_TYPE,
            5,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(Items.OAK_LEAVES, Items.BIRCH_LEAVES, Items.SPRUCE_LEAVES, Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES, Items.DARK_OAK_LEAVES, Items.AZALEA_LEAVES, Items.FLOWERING_AZALEA_LEAVES),
            layers,
            0.0F,
            0.0F
    ));
}

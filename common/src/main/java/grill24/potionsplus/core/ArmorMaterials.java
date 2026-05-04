package grill24.potionsplus.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

import static grill24.potionsplus.utility.Utility.ppId;

public class ArmorMaterials {
    private static final Map<ArmorType, Integer> HEALTH_FUNCTION_FOR_TYPE;
    static {
        HEALTH_FUNCTION_FOR_TYPE = new EnumMap<>(ArmorType.class);
        HEALTH_FUNCTION_FOR_TYPE.put(ArmorType.BOOTS, 13);
        HEALTH_FUNCTION_FOR_TYPE.put(ArmorType.LEGGINGS, 15);
        HEALTH_FUNCTION_FOR_TYPE.put(ArmorType.CHESTPLATE, 16);
        HEALTH_FUNCTION_FOR_TYPE.put(ArmorType.HELMET, 11);
    }

    public static ArmorMaterial WREATH = new ArmorMaterial(
            15,
            HEALTH_FUNCTION_FOR_TYPE,
            25,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0F,
            2F,
            ItemTags.LEAVES,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ppId("wreath"))
    );
}

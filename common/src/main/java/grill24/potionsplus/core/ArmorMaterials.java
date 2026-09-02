package grill24.potionsplus.core;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Loader-agnostic armor material hub. The {@code DeferredRegister} lives in the loader module
 * (neoforge's {@code core.neoforge.ArmorMaterials}), which calls {@link #init} to register
 * {@code WREATH}. See docs/multi-loader-expansion.md Phase 4.
 */
public class ArmorMaterials {
    private static Map<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });
    private static List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(ppId("wreath")));

    public static Holder<ArmorMaterial> WREATH;

    public static void init(BiFunction<String, Supplier<ArmorMaterial>, Holder<ArmorMaterial>> register) {
        WREATH = register.apply("wreath", () -> new ArmorMaterial(
                HEALTH_FUNCTION_FOR_TYPE,
                5,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                () -> Ingredient.of(Items.OAK_LEAVES, Items.BIRCH_LEAVES, Items.SPRUCE_LEAVES, Items.JUNGLE_LEAVES, Items.ACACIA_LEAVES, Items.DARK_OAK_LEAVES, Items.AZALEA_LEAVES, Items.FLOWERING_AZALEA_LEAVES),
                layers,
                0.0F,
                0.0F
        ));
    }
}

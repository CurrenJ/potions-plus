package grill24.potionsplus.core.items;

import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.GenericIconItemBuilder;
import grill24.potionsplus.utility.registration.item.ItemOverrideUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class DynamicIconItems {
    // ----- Dynamically Rendered Display Items -----
    public static final Identifier DYNAMIC_ICON_INDEX_PROPERTY_NAME = ppId("dynamic_icon_index");
    public static SimpleItemBuilder POTION_EFFECT_ICON;

    public static final Identifier AMP_TEX_LOC = ppId("item/amplifier_upgrade");
    public static final Identifier DUR_TEX_LOC = ppId("item/duration_upgrade");
    public static final Identifier I_TEX_LOC = ppId("item/i");
    public static final Identifier II_TEX_LOC = ppId("item/ii");
    public static final Identifier III_TEX_LOC = ppId("item/iii");
    public static final Identifier IV_TEX_LOC = ppId("item/iv");
    public static final Identifier V_TEX_LOC = ppId("item/v");
    public static final Identifier VI_TEX_LOC = ppId("item/vi");
    public static final Identifier VII_TEX_LOC = ppId("item/vii");
    public static final Identifier VIII_TEX_LOC = ppId("item/viii");
    public static final Identifier ARROW_TEX_LOC = ppId("item/arrow");
    public static final Identifier UNKNOWN_TEX_LOC = ppId("item/unknown");
    public static final Identifier SGA_A_TEX_LOC = mc("particle/sga_a");
    public static final Identifier SGA_B_TEX_LOC = mc("particle/sga_b");
    public static final Identifier SGA_C_TEX_LOC = mc("particle/sga_c");
    public static final Identifier SGA_D_TEX_LOC = mc("particle/sga_d");
    public static final Identifier COMMON_TEX_LOC = ppId("item/common");
    public static final Identifier RARE_TEX_LOC = ppId("item/rare");
    public static final Identifier NO_EXP_TEX_LOC = ppId("item/no_experience");
    public static final Identifier NO_HEAT_TEX_LOC = ppId("item/no_heat");
    public static final Identifier FISHING_BAR_TEX_LOC = ppId("item/fishing_bar");
    public static final Identifier FISHING_BOBBER_TEX_LOC = ppId("item/fishing_bobber");
    public static final Identifier COPPER_FISHING_FRAME_TEX_LOC = ppId("item/copper_fishing_frame");
    public static final Identifier GOLD_FISHING_FRAME_TEX_LOC = ppId("item/gold_fishing_frame");
    public static final Identifier DIAMOND_FISHING_FRAME_TEX_LOC = ppId("item/diamond_fishing_frame");
    public static final Identifier PURPLE_FISHING_FRAME_TEX_LOC = ppId("item/purple_fishing_frame");
    public static final Identifier GOLD_SELECTION_FRAME_TEX_LOC = ppId("item/gold_selection_frame");
    public static final Identifier GLOBAL_TEX_LOC = ppId("item/global");
    public static final Identifier RULER_TEX_LOC = ppId("item/ruler");
    public static final Identifier COUNT_TEX_LOC = ppId("item/count");
    public static final Identifier GIFT_TEX_LOC = ppId("item/gift");
    public static GenericIconItemBuilder GENERIC_ICON;

    /**
     * Force static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        POTION_EFFECT_ICON = RegistrationUtility.register(register, SimpleItemBuilder.create("potion_effect_icon")
                .itemFactory(Item::new)
                .modelGenerator((itemHolder) -> new ItemOverrideUtility.PotionEffectIconOverrideModelData(itemHolder, DYNAMIC_ICON_INDEX_PROPERTY_NAME)));

        GENERIC_ICON = RegistrationUtility.register(register, new GenericIconItemBuilder(DYNAMIC_ICON_INDEX_PROPERTY_NAME,
                AMP_TEX_LOC, DUR_TEX_LOC, I_TEX_LOC, II_TEX_LOC, III_TEX_LOC, IV_TEX_LOC, V_TEX_LOC, VI_TEX_LOC,
                VII_TEX_LOC, VIII_TEX_LOC, ARROW_TEX_LOC, UNKNOWN_TEX_LOC, SGA_A_TEX_LOC, SGA_B_TEX_LOC,
                SGA_C_TEX_LOC, SGA_D_TEX_LOC, COMMON_TEX_LOC, RARE_TEX_LOC, NO_EXP_TEX_LOC,
                NO_HEAT_TEX_LOC, FISHING_BAR_TEX_LOC, FISHING_BOBBER_TEX_LOC,
                COPPER_FISHING_FRAME_TEX_LOC, GOLD_FISHING_FRAME_TEX_LOC,
                DIAMOND_FISHING_FRAME_TEX_LOC, PURPLE_FISHING_FRAME_TEX_LOC,
                GOLD_SELECTION_FRAME_TEX_LOC, GLOBAL_TEX_LOC,
                RULER_TEX_LOC, COUNT_TEX_LOC, GIFT_TEX_LOC).name("generic_icon"));
    }
}

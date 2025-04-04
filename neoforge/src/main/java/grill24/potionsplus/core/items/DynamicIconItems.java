package grill24.potionsplus.core.items;

import grill24.potionsplus.utility.registration.item.ItemOverrideUtility;
import grill24.potionsplus.utility.registration.item.GenericIconItemBuilder;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class DynamicIconItems {
    // ----- Dynamically Rendered Display Items -----
    public static final ResourceLocation DYNAMIC_ICON_INDEX_PROPERTY_NAME = ppId("dynamic_icon_index");
    public static SimpleItemBuilder POTION_EFFECT_ICON;

    public static final ResourceLocation AMP_TEX_LOC = ppId("item/amplifier_upgrade");
    public static final ResourceLocation DUR_TEX_LOC = ppId("item/duration_upgrade");
    public static final ResourceLocation I_TEX_LOC = ppId("item/i");
    public static final ResourceLocation II_TEX_LOC = ppId("item/ii");
    public static final ResourceLocation III_TEX_LOC = ppId("item/iii");
    public static final ResourceLocation IV_TEX_LOC = ppId("item/iv");
    public static final ResourceLocation V_TEX_LOC = ppId("item/v");
    public static final ResourceLocation VI_TEX_LOC = ppId("item/vi");
    public static final ResourceLocation VII_TEX_LOC = ppId("item/vii");
    public static final ResourceLocation VIII_TEX_LOC = ppId("item/viii");
    public static final ResourceLocation ARROW_TEX_LOC = ppId("item/arrow");
    public static final ResourceLocation UNKNOWN_TEX_LOC = ppId("item/unknown");
    public static final ResourceLocation SGA_A_TEX_LOC = mc("particle/sga_a");
    public static final ResourceLocation SGA_B_TEX_LOC = mc("particle/sga_b");
    public static final ResourceLocation SGA_C_TEX_LOC = mc("particle/sga_c");
    public static final ResourceLocation SGA_D_TEX_LOC = mc("particle/sga_d");
    public static final ResourceLocation COMMON_TEX_LOC = ppId("item/common");
    public static final ResourceLocation RARE_TEX_LOC = ppId("item/rare");
    public static final ResourceLocation NO_EXP_TEX_LOC = ppId("item/no_experience");
    public static final ResourceLocation NO_HEAT_TEX_LOC = ppId("item/no_heat");
    public static final ResourceLocation FISHING_BAR_TEX_LOC = ppId("item/fishing_bar");
    public static final ResourceLocation FISHING_BOBBER_TEX_LOC = ppId("item/fishing_bobber");
    public static final ResourceLocation COPPER_FISHING_FRAME_TEX_LOC = ppId("item/copper_fishing_frame");
    public static final ResourceLocation GOLD_FISHING_FRAME_TEX_LOC = ppId("item/gold_fishing_frame");
    public static final ResourceLocation DIAMOND_FISHING_FRAME_TEX_LOC = ppId("item/diamond_fishing_frame");
    public static final ResourceLocation PURPLE_FISHING_FRAME_TEX_LOC = ppId("item/purple_fishing_frame");
    public static final ResourceLocation GOLD_SELECTION_FRAME_TEX_LOC = ppId("item/gold_selection_frame");
    public static final ResourceLocation GLOBAL_TEX_LOC = ppId("item/global");
    public static final ResourceLocation RULER_TEX_LOC = ppId("item/ruler");
    public static final ResourceLocation COUNT_TEX_LOC = ppId("item/count");
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
                RULER_TEX_LOC, COUNT_TEX_LOC).name("generic_icon"));
    }
}

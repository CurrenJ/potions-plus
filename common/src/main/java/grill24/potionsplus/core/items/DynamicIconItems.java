package grill24.potionsplus.core.items;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Loader-agnostic stub for the dynamic-icon item statics (Phase 11a step 3). Mirrors the
 * {@code BrewingItems}/{@code OreItems}/{@code WreathItem} pattern established in Phase 4: the
 * texture-index bookkeeping and runtime icon-lookup helpers live here (needed by all three
 * loaders, e.g. by block entities building status-icon {@link ItemStack}s), while the actual
 * {@code Item} registration + item-model datagen (predicate overrides per icon) stays behind each
 * loader's own {@code init}. NeoForge remains the sole datagen source of truth (Decision 5) via
 * its own {@code core.neoforge.items.DynamicIconItems}, which still drives the
 * {@code RegistrationUtility}/{@code AbstractRegistererBuilder} DSL to also generate the item
 * models; Fabric/Forge only need the two {@code Item}s to exist (registered directly, no DSL,
 * matching {@code BrewingItems}), since the generated models are shared via {@code commonDatagen}
 * (Phase 10). See docs/multi-loader-expansion.md Phase 11a.
 */
public class DynamicIconItems {
    public static final ResourceLocation DYNAMIC_ICON_INDEX_PROPERTY_NAME = ppId("dynamic_icon_index");

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
    public static final ResourceLocation GOLD_FISHING_FRAME_TEX_LOC = ppId("item/gold_fishing_frame");
    public static final ResourceLocation GOLD_SELECTION_FRAME_TEX_LOC = ppId("item/gold_selection_frame");
    public static final ResourceLocation GLOBAL_TEX_LOC = ppId("item/global");
    public static final ResourceLocation RULER_TEX_LOC = ppId("item/ruler");
    public static final ResourceLocation COUNT_TEX_LOC = ppId("item/count");

    /**
     * Order must match {@code core.neoforge.items.DynamicIconItems}' {@code GenericIconItemBuilder}
     * construction exactly - the index of a texture here is baked into the generated item model's
     * override predicate value (NeoForge datagen) and into the stack count this class hands back
     * for it (all loaders), so the two must never drift apart.
     */
    public static final List<ResourceLocation> GENERIC_ICON_TEXTURES = List.of(
            AMP_TEX_LOC, DUR_TEX_LOC, I_TEX_LOC, II_TEX_LOC, III_TEX_LOC, IV_TEX_LOC, V_TEX_LOC, VI_TEX_LOC,
            VII_TEX_LOC, VIII_TEX_LOC, ARROW_TEX_LOC, UNKNOWN_TEX_LOC, SGA_A_TEX_LOC, SGA_B_TEX_LOC,
            SGA_C_TEX_LOC, SGA_D_TEX_LOC, COMMON_TEX_LOC, RARE_TEX_LOC, NO_EXP_TEX_LOC,
            NO_HEAT_TEX_LOC, GOLD_FISHING_FRAME_TEX_LOC,
            GOLD_SELECTION_FRAME_TEX_LOC, GLOBAL_TEX_LOC,
            RULER_TEX_LOC, COUNT_TEX_LOC);

    private static final Map<ResourceLocation, Integer> GENERIC_ICON_INDEX = new HashMap<>();
    static {
        for (int i = 0; i < GENERIC_ICON_TEXTURES.size(); i++) {
            GENERIC_ICON_INDEX.put(GENERIC_ICON_TEXTURES.get(i), i);
        }
    }

    public static Holder<Item> POTION_EFFECT_ICON;
    public static Holder<Item> GENERIC_ICON;

    public static int getGenericIconIndex(ResourceLocation textureLocation) {
        return GENERIC_ICON_INDEX.getOrDefault(textureLocation, 0);
    }

    public static int getGenericIconItemStackCountForTexture(ResourceLocation textureLocation) {
        return getGenericIconIndex(textureLocation) + 1;
    }

    public static ItemStack getGenericIconItemStackForTexture(ResourceLocation textureLocation) {
        return new ItemStack(GENERIC_ICON.value(), getGenericIconItemStackCountForTexture(textureLocation));
    }
}

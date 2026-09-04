package grill24.potionsplus.item.tintsource;

import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Shared item-tint logic for potion items (rainbow-cycles for "any potion" placeholder effects,
 * otherwise the potion's own color). 1.21.1 predates the {@code ItemTintSource} codec system (that's
 * a later-MC feature - see docs/multi-loader-expansion.md Phase 11), so each loader still registers
 * this against its own classic {@code ItemColor} entry point (NeoForge/Forge:
 * {@code RegisterColorHandlersEvent.Item}; Fabric: {@code ColorProviderRegistry.ITEM}) - this class
 * is the one piece of that logic that's actually shareable.
 */
public final class PotionsPlusItemColors {
    private PotionsPlusItemColors() {
    }

    /** Mirrors the vanilla {@code ItemColor}/{@code ColorProvider} signature: {@code (stack, tintIndex) -> argb}. */
    public static int anyPotionItemColor(ItemStack stack, int tintIndex) {
        if (tintIndex > 0) {
            return -1;
        }

        PotionContents potionContents = PotionData.read(stack).toContents();

        boolean isAnyPotion = false;
        for (MobEffectInstance effect : potionContents.getAllEffects()) {
            isAnyPotion = effect.getEffect().is(MobEffects.ANY_POTION) || effect.getEffect().is(MobEffects.ANY_OTHER_POTION);
            if (isAnyPotion) {
                break;
            }
        }
        if (!isAnyPotion) {
            return FastColor.ARGB32.opaque(PotionData.read(stack).toContents().getColor());
        }

        float ticks = ClientTickHandler.total();
        // Rainbow over time.
        int r = (int) (Math.sin(ticks * 0.01f) * 127 + 128);
        int g = (int) (Math.sin(ticks * 0.01f + 2.0943951023931953) * 127 + 128);
        int b = (int) (Math.sin(ticks * 0.01f + 4.1887902047863905) * 127 + 128);
        return FastColor.ARGB32.color(r, g, b);
    }
}

package grill24.potionsplus.item.tintsource;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

public class AnyPotionTintSource implements ItemTintSource {
    public static final ResourceLocation ID = ppId("any_potion_tint");
    public static final MapCodec<AnyPotionTintSource> CODEC = MapCodec.unit(new AnyPotionTintSource());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        PotionContents potionContents = PUtil.getPotionContents(stack);

        boolean isAnyPotion = false;
        for (MobEffectInstance effect : potionContents.getAllEffects()) {
            isAnyPotion = effect.getEffect().is(MobEffects.ANY_POTION) || effect.getEffect().is(MobEffects.ANY_OTHER_POTION);
            if (isAnyPotion) {
                break;
            }
        }
        if (!isAnyPotion) {
            return ARGB.opaque(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor());
        }

        float ticks = ClientTickHandler.total();
        // returning int rgb int value - rainbow over time
        int r = (int) (Math.sin(ticks * 0.01f) * 127 + 128);
        int g = (int) (Math.sin(ticks * 0.01f + 2.0943951023931953) * 127 + 128);
        int b = (int) (Math.sin(ticks * 0.01f + 4.1887902047863905) * 127 + 128);
        return ARGB.color(r, g, b);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}

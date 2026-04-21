package grill24.potionsplus.item.tintsource;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.item.GeneticCropItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

public class GeneticCropItemTintSource implements ItemTintSource {
    public static final ResourceLocation ID = ppId("genetic_crop_tint");
    public static final MapCodec<GeneticCropItemTintSource> CODEC = MapCodec.unit(new GeneticCropItemTintSource());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof GeneticCropItem geneticCropItem) {
            return geneticCropItem.getColorARGB(stack);
        }

        return ARGB.white(1F);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}

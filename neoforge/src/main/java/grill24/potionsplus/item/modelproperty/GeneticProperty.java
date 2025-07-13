package grill24.potionsplus.item.modelproperty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public record GeneticProperty(int chromosomeIndex) implements RangeSelectItemModelProperty {
    public static final ResourceLocation ID = ppId("genetic");
    public static final MapCodec<GeneticProperty> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("chromosome_index").forGetter(GeneticProperty::chromosomeIndex)
    ).apply(instance, GeneticProperty::new));

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
        if (stack.has(DataComponents.GENETIC_DATA) && stack.getItem() instanceof GeneticCropItem geneticCropItem) {
            return geneticCropItem.getChromosomeValueNormalized(stack, chromosomeIndex);
        }
        return 0.0F;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }

    @SubscribeEvent
    public static void registerProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ID, MAP_CODEC);
    }
}

package grill24.potionsplus.item.modelproperty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.item.BrassicaOleraceaItem;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public record BrassicaOleraceaProperty() implements SelectItemModelProperty<BrassicaOleraceaItem.Variation> {
    public static final ResourceLocation ID = ppId("genetic");
    public static final Type<BrassicaOleraceaProperty, BrassicaOleraceaItem.Variation> MAP_CODEC = Type.create(
            MapCodec.unit(BrassicaOleraceaProperty::new),
            StringRepresentable.fromEnum(BrassicaOleraceaItem.Variation::values)
    );

    @Override
    public @Nullable BrassicaOleraceaItem.Variation get(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed, ItemDisplayContext itemDisplayContext) {
        if (stack.has(DataComponents.GENETIC_DATA) && stack.getItem() instanceof BrassicaOleraceaItem brassicaOleraceaItem) {
            return brassicaOleraceaItem.getVariation(stack);
        }

        return BrassicaOleraceaItem.Variation.BRASSICA_OLERACEA;
    }

    @Override
    public Codec<BrassicaOleraceaItem.Variation> valueCodec() {
        return StringRepresentable.fromEnum(BrassicaOleraceaItem.Variation::values);
    }

    @Override
    public Type<? extends SelectItemModelProperty<BrassicaOleraceaItem.Variation>, BrassicaOleraceaItem.Variation> type() {
        return MAP_CODEC;
    }

    @SubscribeEvent
    public static void registerProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(ID, MAP_CODEC);
    }
}

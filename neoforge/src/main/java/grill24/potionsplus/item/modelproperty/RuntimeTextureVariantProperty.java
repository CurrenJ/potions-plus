package grill24.potionsplus.item.modelproperty;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.item.RuntimeVariantItemDataComponent;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public record RuntimeTextureVariantProperty() implements RangeSelectItemModelProperty {
    public static final ResourceLocation ID = ppId("runtime_texture_variant");

    public static final MapCodec<RuntimeTextureVariantProperty> MAP_CODEC = MapCodec.unit(new RuntimeTextureVariantProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
        return stack.getOrDefault(DataComponents.RUNTIME_VARIANT_ITEM, new RuntimeVariantItemDataComponent(0.0F)).propertyValue();
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

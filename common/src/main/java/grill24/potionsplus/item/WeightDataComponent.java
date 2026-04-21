package grill24.potionsplus.item;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.item.tooltip.TooltipPriorities;
import grill24.potionsplus.utility.ModInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public record WeightDataComponent(float weight) {
    public static final Codec<WeightDataComponent> CODEC = Codec.FLOAT.xmap(WeightDataComponent::new, WeightDataComponent::weight);
    public static final StreamCodec<ByteBuf, WeightDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            WeightDataComponent::weight,
            WeightDataComponent::new
    );

    public Component getWeightText() {
        // Trim float to 1 decimal places
        String size = String.format("%.1f", this.weight);
        return Component.translatable(Translations.TOOLTIP_TRAIT_WEIGHT_GRAMS, size);
    }

    @SubscribeEvent
    public static void onTooltip(final AnimatedItemTooltipEvent.Add event) {
        // Fish Size Data Component Tooltip
        ItemStack stack = event.getItemStack();
        if (stack.has(grill24.potionsplus.core.DataComponents.WEIGHT)) {
            WeightDataComponent weightData = stack.get(grill24.potionsplus.core.DataComponents.WEIGHT);
            if (weightData != null) {
                AnimatedItemTooltipEvent.TooltipLines tooltipLines = AnimatedItemTooltipEvent.TooltipLines.of(ppId("weight"), TooltipPriorities.SIZE, weightData.getWeightText());
                event.addTooltipMessage(tooltipLines);
            }
        }
    }
}

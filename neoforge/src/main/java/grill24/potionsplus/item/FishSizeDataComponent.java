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
public record FishSizeDataComponent(float size) {
    public static final Codec<FishSizeDataComponent> CODEC = Codec.FLOAT.xmap(FishSizeDataComponent::new, FishSizeDataComponent::size);
    public static final StreamCodec<ByteBuf, FishSizeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            FishSizeDataComponent::size,
            FishSizeDataComponent::new
    );

    public Component getSizeText() {
        // Trim float to 1 decimal places
        String size = String.format("%.1f", this.size);
        return Component.translatable(Translations.FISH_SIZE, size);
    }

    public float getItemFrameSizeMultiplier() {
        // Item Frames render items at 0.5 block size.
        float sizeMeters = this.size / 100F; // 1 meter = 1 block
        return sizeMeters * 2F;
    }

    @SubscribeEvent
    public static void onTooltip(final AnimatedItemTooltipEvent.Add event) {
        // Fish Size Data Component Tooltip
        ItemStack stack = event.getItemStack();
        if (stack.has(grill24.potionsplus.core.DataComponents.FISH_SIZE)) {
            FishSizeDataComponent fishSizeData = stack.get(grill24.potionsplus.core.DataComponents.FISH_SIZE);
            if (fishSizeData != null) {
                AnimatedItemTooltipEvent.TooltipLines tooltipLines = AnimatedItemTooltipEvent.TooltipLines.of(ppId("fish_size"), TooltipPriorities.SIZE, fishSizeData.getSizeText());
                event.addTooltipMessage(tooltipLines);
            }
        }
    }
}

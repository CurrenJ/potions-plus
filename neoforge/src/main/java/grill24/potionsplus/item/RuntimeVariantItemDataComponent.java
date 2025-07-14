package grill24.potionsplus.item;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record RuntimeVariantItemDataComponent(float propertyValue) {
    public static final Codec<RuntimeVariantItemDataComponent> CODEC = Codec.FLOAT.xmap(RuntimeVariantItemDataComponent::new, RuntimeVariantItemDataComponent::propertyValue);
    public static final StreamCodec<ByteBuf, RuntimeVariantItemDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            RuntimeVariantItemDataComponent::propertyValue,
            RuntimeVariantItemDataComponent::new
    );

    public static ItemStack getStackFromBlockState(BlockState blockState, Property<Integer> textureVariantProperty) {
        if (blockState.hasProperty(textureVariantProperty)) {
            RuntimeVariantItemDataComponent runtimeVariantItemDataComponent = new RuntimeVariantItemDataComponent(
                    getThresholdValueForBlockState(blockState, textureVariantProperty));

            ItemStack stack = new ItemStack(blockState.getBlock());
            stack.set(DataComponents.RUNTIME_VARIANT_ITEM, runtimeVariantItemDataComponent);

            return stack;
        } else {
            throw new IllegalArgumentException("BlockState does not have the specified property: " + textureVariantProperty.getName());
        }
    }

    public static float getThresholdValueForBlockState(BlockState state, Property<Integer> property) {
        int value = state.getValue(property);
        return ((float) value / (PotionsPlusOreBlock.TEXTURE.getPossibleValues().size() - 1)) + 0.0001F;
    }

    public static int getTextureVariantFromStack(ItemStack stack, Property<Integer> textureVariantProperty) {
        if (stack.has(DataComponents.RUNTIME_VARIANT_ITEM)) {
            RuntimeVariantItemDataComponent runtimeVariant = stack.getOrDefault(DataComponents.RUNTIME_VARIANT_ITEM, new RuntimeVariantItemDataComponent(0.0F));
            int textureVariantIndex = (int) ((runtimeVariant.propertyValue() * (textureVariantProperty.getPossibleValues().size() - 1)) + 0.0001F);
            if (textureVariantIndex < 0 || textureVariantIndex >= textureVariantProperty.getPossibleValues().size()) {
                PotionsPlus.LOGGER.warn("Texture variant index {} is out of bounds for property {}", textureVariantIndex, textureVariantProperty.getName());
            }
            return textureVariantIndex;
        } else {
            throw new IllegalArgumentException("ItemStack does not have the RuntimeVariantItemDataComponent");
        }
    }

    public static BlockState getBlockStateFromStack(ItemStack stack, Property<Integer> textureVariantProperty) {
        if (stack.has(DataComponents.RUNTIME_VARIANT_ITEM) && stack.getItem() instanceof BlockItem blockItem) {
            int textureVariantPropertyValue = getTextureVariantFromStack(stack, textureVariantProperty);
            return blockItem.getBlock().defaultBlockState().setValue(textureVariantProperty, textureVariantPropertyValue);
        } else {
            PotionsPlus.LOGGER.error("ItemStack does not have the RuntimeVariantItemDataComponent, returning default block state");
            return Blocks.AIR.defaultBlockState();
        }
    }
}

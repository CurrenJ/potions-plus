package grill24.potionsplus.item.consumeeffect;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.ConsumeEffects;
import grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public class EdibleChoiceItemConsumeEffect implements ConsumeEffect {
    public static final EdibleChoiceItemConsumeEffect INSTANCE = new EdibleChoiceItemConsumeEffect();
    public static final MapCodec<EdibleChoiceItemConsumeEffect> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, EdibleChoiceItemConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return ConsumeEffects.EDIBLE_CHOICE_ITEM.value();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            EdibleRewardGranterDataComponent.tryEatEdibleChoiceItem(serverPlayer, stack);
        }
        return true;
    }
}

package grill24.potionsplus.item.modelproperty;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.items.SkillLootItems;
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
public record EdibleChoiceProperty() implements RangeSelectItemModelProperty {
    public static final ResourceLocation ID = ppId("edible_choice");
    public static final MapCodec<EdibleChoiceProperty> MAP_CODEC = MapCodec.unit(new EdibleChoiceProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (stack.has(DataComponents.CHOICE_ITEM)) {
            return SkillLootItems.BASIC_LOOT.getItemOverrideData().getOverrideValue(stack.get(DataComponents.CHOICE_ITEM).flag());
        }
        return 0.0F;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }

    @SubscribeEvent
    public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ID, MAP_CODEC);
    }
}

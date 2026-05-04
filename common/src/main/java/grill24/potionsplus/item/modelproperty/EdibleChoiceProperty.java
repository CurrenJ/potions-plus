package grill24.potionsplus.item.modelproperty;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.items.SkillLootItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static grill24.potionsplus.utility.Utility.ppId;

public record EdibleChoiceProperty() implements RangeSelectItemModelProperty {
    public static final Identifier ID = ppId("edible_choice");
    public static final MapCodec<EdibleChoiceProperty> MAP_CODEC = MapCodec.unit(new EdibleChoiceProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner entity, int seed) {
        if (stack.has(DataComponents.CHOICE_ITEM.get())) {
            return SkillLootItems.BASIC_LOOT.getItemOverrideData().getOverrideValue(stack.get(DataComponents.CHOICE_ITEM.get()).flag());
        }
        return 0.0F;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }

}

package grill24.potionsplus.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.LootItemFunctions;
import grill24.potionsplus.item.FishSizeDataComponent;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Set;

public class SetFishSizeFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFishSizeFunction> CODEC = RecordCodecBuilder.mapCodec(
            codecBuilder -> commonFields(codecBuilder)
                    .and(NumberProviders.CODEC.fieldOf("size").forGetter(instance -> instance.size))
                    .apply(codecBuilder, SetFishSizeFunction::new)
    );

    private final NumberProvider size;

    SetFishSizeFunction(List<LootItemCondition> condtions, NumberProvider size) {
        super(condtions);
        this.size = size;
    }

    @Override
    public LootItemFunctionType<SetFishSizeFunction> getType() {
        return LootItemFunctions.SET_FISH_SIZE.value();
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.size.getReferencedContextParams();
    }

    /**
     * Called to perform the actual action of this function, after conditions have been checked.
     */
    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        stack.set(DataComponents.FISH_SIZE, new FishSizeDataComponent(Math.max(0.001F, this.size.getFloat(context))));
        return stack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetFishSizeFunction.Builder> {
        private final NumberProvider size;

        public Builder(NumberProvider size) {
            this.size = size;
        }

        protected SetFishSizeFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetFishSizeFunction(this.getConditions(), this.size);
        }
    }
}

package grill24.potionsplus.item.consumeeffect;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.ConsumeEffects;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.utility.NewGenotype;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public class GeneticCropItemConsumeEffect implements ConsumeEffect {
    public static final GeneticCropItemConsumeEffect INSTANCE = new GeneticCropItemConsumeEffect();
    public static final MapCodec<GeneticCropItemConsumeEffect> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, GeneticCropItemConsumeEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return ConsumeEffects.EDIBLE_CHOICE_ITEM.value();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        if (stack.has(DataComponents.GENETIC_DATA) && entity instanceof ServerPlayer serverPlayer && stack.getItem() instanceof GeneticCropItem geneticCropItem) {
            NewGenotype genotype = stack.get(DataComponents.GENETIC_DATA);

            // Get byte associated with nutrition chromosome
            byte[] chromosomes = genotype.getGenotypeAsBytes();
            byte n = chromosomes.length > GeneticCropItem.NUTRITION_CHROMOSOME_INDEX ? 
                     chromosomes[GeneticCropItem.NUTRITION_CHROMOSOME_INDEX] : 0;

            float nutrition = geneticCropItem.getChromosomeValueNormalized(stack, GeneticCropItem.NUTRITION_CHROMOSOME_INDEX) * 9F + 1F;
//            float saturation = geneticCropItem.getChromosomeValueNormalized(stack, GeneticCropItem.SATURATION_CHROMOSOME_INDEX);
            float saturation = 0.1F;

            FoodProperties foodProperties = new FoodProperties.Builder().nutrition(Math.round(nutrition)).saturationModifier(saturation).build();
            serverPlayer.getFoodData().eat(foodProperties);
        }
        return true;
    }
}

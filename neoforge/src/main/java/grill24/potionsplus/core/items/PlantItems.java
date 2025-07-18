package grill24.potionsplus.core.items;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.item.BrassicaOleraceaItem;
import grill24.potionsplus.item.TomatoItem;
import grill24.potionsplus.item.tintsource.GeneticCropItemTintSource;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class PlantItems {
    public static Holder<Item> TOMATO, BRASSICA_OLERACEA, BRUSSELS_SPROUTS, CABBAGE, BROCCOLLI, CAULIFLOWER, KALE, KOHLRABI;

    /**
     * Force static fields to be initialized
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        TOMATO = RegistrationUtility.register(register, SimpleItemBuilder.create("tomato")
                .itemFactory(prop -> new TomatoItem(prop.food(Foods.CARROT)))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/tomato_dithering"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, true,
                                ppId("item/tomato_small_tint"), ppId("item/tomato_small_sheen"), ppId("item/tomato_small_stem")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.4F, true,
                                ppId("item/tomato_medium_tint_dithered"), ppId("item/tomato_medium_sheen"), ppId("item/tomato_medium_stem")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.85F, true,
                                ppId("item/tomato_tint_dithering"), ppId("item/tomato_sheen"), ppId("item/tomato_stem"))
                ))
        ).getHolder();

        BRASSICA_OLERACEA = RegistrationUtility.register(register, SimpleItemBuilder.create("brassica_oleracea")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BRASSICA_OLERACEA_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/brassica_oleracea"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/brassica_oleracea_small")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.45F, false,
                                ppId("item/brassica_oleracea"))
                ))
        ).getHolder();

        CABBAGE = RegistrationUtility.register(register, SimpleItemBuilder.create("cabbage")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.CABBAGE_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/cabbage"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/cabbage"))
                ))
        ).getHolder();

        KALE = RegistrationUtility.register(register, SimpleItemBuilder.create("kale")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.KALE_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/kale"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/kale_small")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.4F, false,
                                ppId("item/kale")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.85F, false,
                                ppId("item/kale_large"))
                ))
        ).getHolder();

        BROCCOLLI = RegistrationUtility.register(register, SimpleItemBuilder.create("broccoli")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BROCCOLLI_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/broccoli"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/broccoli")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.5F, false,
                                ppId("item/broccoli_large"))
                ))
        ).getHolder();

        CAULIFLOWER = RegistrationUtility.register(register, SimpleItemBuilder.create("cauliflower")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.CAULIFLOWER_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/cauliflower"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/cauliflower"))
                ))
        ).getHolder();

        BRUSSELS_SPROUTS = RegistrationUtility.register(register, SimpleItemBuilder.create("brussels_sprouts")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BRUSSELS_SPROUTS_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/brussel_sprouts"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/brussel_sprouts_small")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.4F, false,
                                ppId("item/brussel_sprouts")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.85F, false,
                                ppId("item/brussel_sprouts_large"))
                ))
        ).getHolder();

        KOHLRABI = RegistrationUtility.register(register, SimpleItemBuilder.create("kohlrabi")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.KOHLRABI_PLANT.value()))
                .modelGenerator(holder -> new ItemModelUtility.GeneticCropWeightOverrideModelGenerator<>(holder,
                        ppId("item/kohlrabi"),
                        GeneticCropItemTintSource::new,
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0, false,
                                ppId("item/kohlrabi_small")),
                        new ItemModelUtility.GeneticCropWeightOverrideModelGenerator.ModelData(0.6F, false,
                                ppId("item/kohlrabi"))
                ))
        ).getHolder();
    }
}

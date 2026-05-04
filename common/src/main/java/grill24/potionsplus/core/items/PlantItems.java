package grill24.potionsplus.core.items;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.item.BrassicaOleraceaItem;
import grill24.potionsplus.item.TomatoItem;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PlantItems {
    public static Holder<Item> TOMATO, BRASSICA_OLERACEA, BRUSSELS_SPROUTS, CABBAGE, BROCCOLI, CAULIFLOWER, KALE, KOHLRABI;

    /**
     * Force static fields to be initialized
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        TOMATO = RegistrationUtility.register(register, SimpleItemBuilder.create("tomato")
                .itemFactory(TomatoItem::new)
                .modelGenerator(null)
        ).getHolder();

        BRASSICA_OLERACEA = RegistrationUtility.register(register, SimpleItemBuilder.create("brassica_oleracea")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BRASSICA_OLERACEA_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        CABBAGE = RegistrationUtility.register(register, SimpleItemBuilder.create("cabbage")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.CABBAGE_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        KALE = RegistrationUtility.register(register, SimpleItemBuilder.create("kale")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.KALE_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        BROCCOLI = RegistrationUtility.register(register, SimpleItemBuilder.create("broccoli")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BROCCOLLI_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        CAULIFLOWER = RegistrationUtility.register(register, SimpleItemBuilder.create("cauliflower")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.CAULIFLOWER_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        BRUSSELS_SPROUTS = RegistrationUtility.register(register, SimpleItemBuilder.create("brussels_sprouts")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.BRUSSELS_SPROUTS_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();

        KOHLRABI = RegistrationUtility.register(register, SimpleItemBuilder.create("kohlrabi")
                .itemFactory(prop -> new BrassicaOleraceaItem(prop, FlowerBlocks.KOHLRABI_PLANT.value()))
                .modelGenerator(null)
        ).getHolder();
    }
}

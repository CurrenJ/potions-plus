package grill24.potionsplus.data;

import grill24.potionsplus.core.Potions;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.*;

public class LangProvider extends LanguageProvider {
    public LangProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        addRegistered(grill24.potionsplus.core.Items.ITEMS, this::addItem);
        addRegistered(Potions.POTIONS, this::addPotion);
    }

    private <T> void addRegistered(DeferredRegister<T> register, Consumer<T> consumer) {
        for (RegistryObject<T> obj : register.getEntries()) {
            if(obj.isPresent()) {
                consumer.accept(obj.get());
            }
            else {
                PotionsPlus.LOGGER.warn("RegistryObject<T> not valid: " + obj.getId());
            }
        }
    }

    private void addItem(Item item) {
        String id = snakeToTitle(item.getRegistryName().getPath());
        add(item, id);
    }

    private void addPotion(Potion potion) {
        String id = potion.getRegistryName().getPath();
        String titleText = snakeToTitle(id);

        add("effect." + ModInfo.MOD_ID + "." + id, titleText);
        add(createPotionItemStack(potion, Utility.PotionType.POTION), getPotionName(PotionType.POTION, titleText));
        add(createPotionItemStack(potion, Utility.PotionType.SPLASH_POTION), getPotionName(PotionType.SPLASH_POTION, titleText));
        add(createPotionItemStack(potion, Utility.PotionType.LINGERING_POTION), getPotionName(PotionType.LINGERING_POTION, titleText));
        add(createPotionItemStack(potion, Utility.PotionType.TIPPED_ARROW), getPotionName(PotionType.TIPPED_ARROW, titleText));
    }
}

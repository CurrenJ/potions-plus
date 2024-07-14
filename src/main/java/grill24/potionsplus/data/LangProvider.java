package grill24.potionsplus.data;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.snakeToTitle;

public class LangProvider extends LanguageProvider {
    public LangProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        addRegistered(grill24.potionsplus.core.Items.ITEMS, this::addItem);
        addRegistered(MobEffects.EFFECTS, this::addEffect);
    }

    private <T> void addRegistered(DeferredRegister<T> register, Consumer<T> consumer) {
        for (RegistryObject<T> obj : register.getEntries()) {
            if (obj.isPresent()) {
                consumer.accept(obj.get());
            } else {
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

        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.POTION), PUtil.getPotionName(PUtil.PotionType.POTION, titleText));
        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.SPLASH_POTION), PUtil.getPotionName(PUtil.PotionType.SPLASH_POTION, titleText));
        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.LINGERING_POTION), PUtil.getPotionName(PUtil.PotionType.LINGERING_POTION, titleText));
        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.TIPPED_ARROW), PUtil.getPotionName(PUtil.PotionType.TIPPED_ARROW, titleText));
    }

    private void addEffect(net.minecraft.world.effect.MobEffect effect) {
        String id = effect.getRegistryName().getPath();
        String titleText = snakeToTitle(id);

        add("effect." + ModInfo.MOD_ID + "." + id, titleText);
        add("item.minecraft." + Items.POTION.getRegistryName().getPath() + ".effect." + id, titleText);
        add("item.minecraft." + Items.SPLASH_POTION.getRegistryName().getPath() + ".effect." + id, titleText);
        add("item.minecraft." + Items.LINGERING_POTION.getRegistryName().getPath() + ".effect." + id, titleText);
        add("item.minecraft." + Items.TIPPED_ARROW.getRegistryName().getPath() + ".effect." + id, titleText);
    }
}

package grill24.potionsplus.data;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.snakeToTitle;

public class LangProvider extends LanguageProvider {
    public LangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        addRegistered(grill24.potionsplus.core.Items.ITEMS, this::addItem);
        addRegistered(MobEffects.EFFECTS, this::addEffect);
    }

    private <T> void addRegistered(DeferredRegister<T> register, Consumer<T> consumer) {
        for (Holder<T> obj : register.getEntries()) {
            if (obj.isBound()) {
                consumer.accept(obj.value());
            } else {
                PotionsPlus.LOGGER.warn("Holder<T> not valid: " + obj.getKey());
            }
        }
    }

    private void addItem(Item item) {
        String id = snakeToTitle(BuiltInRegistries.ITEM.getKey(item).getPath());
        add(item, id);
    }

//    private void addPotion(Potion potion) {
//        String id = potion.getRegistryName().getPath();
//        String titleText = snakeToTitle(id);
//
//        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.POTION), PUtil.getPotionName(PUtil.PotionType.POTION, titleText));
//        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.SPLASH_POTION), PUtil.getPotionName(PUtil.PotionType.SPLASH_POTION, titleText));
//        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.LINGERING_POTION), PUtil.getPotionName(PUtil.PotionType.LINGERING_POTION, titleText));
//        add(PUtil.createPotionItemStack(potion, PUtil.PotionType.TIPPED_ARROW), PUtil.getPotionName(PUtil.PotionType.TIPPED_ARROW, titleText));
//    }

    private void addEffect(net.minecraft.world.effect.MobEffect effect) {
        String id = BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath();
        String titleText = snakeToTitle(id);

        add("effect." + ModInfo.MOD_ID + "." + id, titleText);
        add("item.minecraft." + BuiltInRegistries.ITEM.getKey(Items.POTION).getPath() + ".effect." + id, titleText);
        add("item.minecraft." + BuiltInRegistries.ITEM.getKey(Items.SPLASH_POTION).getPath() + ".effect." + id, titleText);
        add("item.minecraft." + BuiltInRegistries.ITEM.getKey(Items.LINGERING_POTION).getPath() + ".effect." + id, titleText);
        add("item.minecraft." + BuiltInRegistries.ITEM.getKey(Items.TIPPED_ARROW).getPath() + ".effect." + id, titleText);
    }
}

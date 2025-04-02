package grill24.potionsplus.utility;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientItemStacksTooltip implements ClientTooltipComponent {
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final List<ItemStack> items;

    public ClientItemStacksTooltip(List<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getHeight() {
        return isShowing() ? this.backgroundHeight() + 4 : 0;
    }

    @Override
    public int getWidth(Font font) {
        return isShowing() ? this.backgroundWidth() : 0;
    }

    private int backgroundWidth() {
        return isShowing() ? this.gridSizeX() * SLOT_SIZE_X + 2 : 0;
    }

    private int backgroundHeight() {
        return isShowing() ? this.gridSizeY() * SLOT_SIZE_Y + 2 : 0;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if(isShowing()) {
            int xMax = this.gridSizeX();
            int yMax = this.gridSizeY();
            int k = 0;

            for (int yIndex = 0; yIndex < yMax; yIndex++) {
                for (int xIndex = 0; xIndex < xMax; xIndex++) {
                    int j1 = x + xIndex * SLOT_SIZE_X + BORDER_WIDTH;
                    int k1 = y + yIndex * SLOT_SIZE_Y + BORDER_WIDTH;
                    this.renderSlot(j1, k1, k++, guiGraphics, font);
                }
            }
        }
    }

    private boolean isShowing() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null;
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (itemIndex < this.items.size()) {
            ItemStack itemstack = this.items.get(itemIndex);
            if (itemstack.isEmpty()) {
                return;
            }

            if (!itemstack.is(Blocks.BREWING_CAULDRON.value().asItem())) {
                this.blit(guiGraphics, x, y, ClientItemStacksTooltip.Texture.SLOT);

                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    try (Level level = player.level()) {
                        PlayerBrewingKnowledge brewingKnowledge = SavedData.instance.getData(player);
                        boolean isInAbyssalTrove = brewingKnowledge.abyssalTroveContainsIngredient(level, PpIngredient.of(itemstack));
                        boolean isPotion = PUtil.isPotion(itemstack);
                        if (!isInAbyssalTrove && !isPotion && !PotionsPlus.Debug.shouldRevealAllRecipes) {

                            itemstack = Items.GENERIC_ICON.getItemStackForTexture(Items.UNKNOWN_TEX_LOC);
                        }
                    } catch (IOException ignored) {}
                }

            }

            guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
            //guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
        }
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, ClientItemStacksTooltip.Texture texture) {
        guiGraphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h);
    }

    private int gridSizeX() {
        return isShowing() ? Math.clamp(this.items.size(), 1, 9) : 0;
    }

    private int gridSizeY() {
        return isShowing() ? (int) (Math.ceil(this.items.size() / (double) gridSizeX())) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        private Texture(ResourceLocation sprite, int w, int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}

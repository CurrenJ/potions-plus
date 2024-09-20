package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BrewingCauldronRecipeCategory implements IRecipeCategory<BrewingCauldronRecipe> {
    public static final RecipeType<BrewingCauldronRecipe> BREWING_CAULDRON_RECIPE_TYPE = RecipeType.create(ModInfo.MOD_ID, "brewing_cauldron_recipe", BrewingCauldronRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    public static final ResourceLocation BREWING_CAULDRON_CATEGORY = new ResourceLocation(ModInfo.MOD_ID, "brewing_cauldron");

    private static final ResourceLocation RECIPE_GUI = new ResourceLocation(ModInfo.MOD_ID, "textures/gui/brewing_cauldron_recipe.png");

    private static final Map<Integer, Point[]> INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT = new HashMap<>();

    static {
        for (int i = 0; i < BrewingCauldronBlockEntity.CONTAINER_SIZE; i++) {
            INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT.put(i, Utility.getPointsOnACircle(i, 20, 38 - 8, 31 - 8));
        }
    }

    public BrewingCauldronRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(RECIPE_GUI, 0, 0, 128, 62)
                .build();
        icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.BREWING_CAULDRON.get()));
    }

    @Override
    public RecipeType<BrewingCauldronRecipe> getRecipeType() {
        return BREWING_CAULDRON_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.potionsplus.brewing_cauldron");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrewingCauldronRecipe recipe, @NotNull IFocusGroup focuses) {
        final Point[] inputSlotPositions = INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT.get(recipe.getIngredients().size());
        for (int i = 0; i < inputSlotPositions.length; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, inputSlotPositions[i].x, inputSlotPositions[i].y)
                    .setSlotName("input_" + i)
                    .addItemStack(recipe.getIngredients().size() <= i ? ItemStack.EMPTY : recipe.getIngredients().get(i).getItems()[0]);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 23)
                .addItemStack(recipe.getResultItem());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 38 - 8, 31 - 8)
                .setSlotName("cauldron")
                .addItemStack(new ItemStack(Blocks.BREWING_CAULDRON.get()));
    }

    @Override
    public void draw(BrewingCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int mainColor = 0xFF80FF20;
        String text = "";
        if (recipe.isAmpUpgrade()) {
            text = Component.translatable("jei.potionsplus.amp_upgrade").getString();
            mainColor = 0xFFfe70e2;
        } else if (recipe.isDurationUpgrade()) {
            text = Component.translatable("jei.potionsplus.dur_upgrade").getString();
            mainColor = 0xFF5bb6ef;
        }

        if (!text.isBlank()) {
            Minecraft minecraft = Minecraft.getInstance();

            // ARGB
            int shadowColor = 0xFF000000 | (mainColor & 0xFCFCFC) >> 2;
            int width = minecraft.font.width(text);
            int x = background.getWidth() - 2 - width;
            int y = 44;

            // TODO 1.13 match the new GuiRepair style
            guiGraphics.drawString(minecraft.font, text, x + 1, y, shadowColor);
            guiGraphics.drawString(minecraft.font, text, x, y + 1, shadowColor);
            guiGraphics.drawString(minecraft.font, text, x + 1, y + 1, shadowColor);
            guiGraphics.drawString(minecraft.font, text, x, y, mainColor);
        }
    }

}

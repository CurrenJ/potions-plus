package grill24.potionsplus.client.integration.jei;

import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipe;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClotheslineRecipeCategory implements IRecipeCategory<ClotheslineRecipe> {
    public static final RecipeType<ClotheslineRecipe> CLOTHESLINE_RECIPE_TYPE = RecipeType.create(ModInfo.MOD_ID, "clothesline_recipe", ClotheslineRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    public static final ResourceLocation CLOTHESLINE_CATEGORY = ppId("clothesline");

    private static final ResourceLocation RECIPE_GUI = ppId( "textures/gui/brewing_cauldron_recipe.png");

    private static final Map<Integer, Point[]> INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT = new HashMap<>();

    static {
        for (int i = 0; i < BrewingCauldronBlockEntity.CONTAINER_SIZE; i++) {
            INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT.put(i, Utility.getPointsOnACircle(i, 20, 38 - 8, 31 - 8));
        }
    }

    public ClotheslineRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(RECIPE_GUI, 0, 0, 128, 62)
                .build();
        icon = guiHelper.createDrawableItemStack(new ItemStack(BlockEntityBlocks.CLOTHESLINE.value()));
    }

    @Override
    public RecipeType<ClotheslineRecipe> getRecipeType() {
        return CLOTHESLINE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable(Translations.BLOCK_POTIONSPLUS_CLOTHESLINE);
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
    public void setRecipe(IRecipeLayoutBuilder builder, ClotheslineRecipe recipe, @NotNull IFocusGroup focuses) {
        final Point[] inputSlotPositions = INPUT_SLOT_POSITIONS_BY_INGREDIENT_COUNT.get(recipe.getPpIngredients().size());
        int ingredientCount = recipe.getPpIngredients().size();
        for (int i = 0; i < inputSlotPositions.length && i < ingredientCount; i++) {
            ItemStack itemStack = recipe.getPpIngredients().get(i).getItemStack();
            List<ItemStack> itemStacksToDisplay = List.of(itemStack);

            builder.addSlot(RecipeIngredientRole.INPUT, inputSlotPositions[i].x, inputSlotPositions[i].y)
                    .setSlotName("input_" + i)
                    .addItemStacks(itemStacksToDisplay);
        }

        List<ItemStack> resultItemStacks = List.of(recipe.getResult());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 23)
                .addItemStacks(resultItemStacks);

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 38 - 8, 31 - 8)
                .setSlotName("cauldron")
                .addItemStack(new ItemStack(BlockEntityBlocks.CLOTHESLINE.value()));
    }

}

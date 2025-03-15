package grill24.potionsplus.gui.skill;

import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillInstance;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

class SkillIconScreenElement extends ItemStackScreenElement {
    public Holder<ConfiguredSkill<?, ?>> skill;

    public float rotation;
    public float rotationSpeed;

    public float baseScale;
    public float targetBaseScale;

    public boolean isAnyItemHovering;
    public boolean isSelected;
    public boolean isAnyItemSelected;

    public SkillIconScreenElement(Screen screen, Settings settings, Holder<ConfiguredSkill<?, ?>> skill, float baseScale) {
        super(screen, null, settings, ItemStack.EMPTY);

        this.skill = skill;

        this.rotationSpeed = 1F;
        this.baseScale = baseScale;
        this.targetBaseScale = baseScale;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        // Update ItemStack
        Player player = this.screen.getMinecraft().player;
        if (player == null) {
            return;
        }
        this.stack = getDisplayItem(player);

        // Update rotation
        this.rotationSpeed = RUtil.lerp(this.rotationSpeed, isAnyItemHovering ? 0.2F : 0.5F, partialTick * this.settings.animationSpeed());
        if (!this.isHovering()) {
            this.rotation += this.rotationSpeed;
        } else {
            this.rotation = RUtil.lerpAngle(this.rotation, 0F, partialTick * this.settings.animationSpeed());
        }

        // Update scale
        this.baseScale = RUtil.lerp(this.baseScale, !isSelected && isAnyItemSelected ? SkillIconsScreenElement.BASE_SCALE * 0.5F : targetBaseScale, partialTick * this.settings.animationSpeed());
        float animationProgress = getAnimationProgress(SkillIconsScreenElement.HOVER_DURATION, SkillIconsScreenElement.HOVER_DURATION);

        this.setCurrentScale(this.isHovering() ?
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_HOVER).getScale().evaluate(animationProgress) :
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_UNHOVER).getScale().evaluate(animationProgress));
        this.setCurrentScale(this.baseScale * this.getCurrentScale());

        super.onTick(partialTick, mouseX, mouseY);
    }

    @Override
    protected void onMouseExit(int mouseX, int mouseY) {
        this.settings = this.settings.withShowBounds(false);
    }

    @Override
    protected void onMouseEnter(int mouseX, int mouseY) {
        this.settings = this.settings.withShowBounds(true);
    }



    private static final int DEFAULT_PIXEL_SIZE = 16;

    @Override
    protected float getWidth() {
        return DEFAULT_PIXEL_SIZE * getCurrentScale();
    }

    @Override
    protected float getHeight() {
        return DEFAULT_PIXEL_SIZE * getCurrentScale();
    }

    public void setIsAnyItemHovering(boolean isAnyItemHovering) {
        this.isAnyItemHovering = isAnyItemHovering;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void setIsAnyItemSelected(boolean isAnyItemSelected) {
        this.isAnyItemSelected = isAnyItemSelected;
    }

    public ItemStack getDisplayItem(Player player) {
        RegistryAccess registryAccess = player.registryAccess();
        ResourceKey<ConfiguredSkill<?, ?>> skillKey = this.skill.getKey();
        if (skillKey == null) {
            return ItemStack.EMPTY;
        }

        Optional<SkillInstance<?, ?>> skillInstance = SkillsData.getPlayerData(player).getOrCreate(registryAccess, this.skill.getKey());
        if (skillInstance.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int level = skillInstance.get().getLevel(registryAccess);

        return this.skill.value().config().getDisplayIconForLevel(level).copy();
    }

    public float getAnimationProgress(float durationHover, float durationExit) {
        if (mouseEnteredTimestamp != -1) {
            return Math.clamp((ClientTickHandler.total() - mouseEnteredTimestamp) / durationHover, 0, 1);
        } else if (mouseExitedTimestamp != -1) {
            return Math.clamp((ClientTickHandler.total() - mouseExitedTimestamp) / durationExit, 0, 1);
        }
        return 0;
    }
}

package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.gui.IRenderableScreenElement;
import grill24.potionsplus.gui.ScreenElementWithChildren;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.function.Consumer;

public class SkillIconsScreenElement extends ScreenElementWithChildren<SkillIconScreenElement> {
    private final Collection<ResourceKey<ConfiguredSkill<?, ?>>> skills;

    private final Map<Integer, SkillIconScreenElement> itemDisplays;
    private SkillIconScreenElement hoveredItem = null;
    private SkillIconScreenElement selectedItem = null;
    private float selectedTimestamp = -1;
    private float deselectedTimestamp = -1;

    private float wheelRotationSpeed = 1F;
    private float wheelRotation = 0F;

    static final float HOVER_DURATION = 10F;
    static final float BASE_SCALE = 2F;

    private final Consumer<SkillIconScreenElement> onIconSelected;


    public SkillIconsScreenElement(Screen screen, Settings settings, Consumer<SkillIconScreenElement> onIconSelected) {
        super(screen, null, settings);

        this.skills = new ArrayList<>();
        this.itemDisplays = new HashMap<>();

        this.onIconSelected = onIconSelected;

        if (screen.getMinecraft().level != null) {
            initializeItemDisplays(screen, screen.getMinecraft().level.registryAccess());
        }
    }

    @Override
    public Collection<SkillIconScreenElement> getChildren() {
        return itemDisplays == null ? Collections.emptyList() : itemDisplays.values();
    }

    @Override
    protected float getWidth() {
        return getRadius() * 2;
    }

    @Override
    protected float getHeight() {
        return getRadius() * 2;
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        // Update wheel rotation
        tickWheelRotation(partialTick, hoveredItem != null);

        // Set child item display positions
        updatePositions();

        // Update hover and selected state of item displays
        for (SkillIconScreenElement display : itemDisplays.values()) {
            display.setIsAnyItemHovering(hoveredItem != null);
            display.setIsAnyItemSelected(selectedItem != null);
            display.setSelected(display == selectedItem);
        }

        super.onTick(partialTick, mouseX, mouseY);
    }

    private void initializeItemDisplays(Screen screen, RegistryAccess registryAccess) {
        // Build skills list (icons we want to display)
        Set<ResourceKey<ConfiguredSkill<?, ?>>> skills = new HashSet<>() {};
        Collection<AbilityInstanceSerializable<?, ?>> abilities = getAbilities();
        for (AbilityInstanceSerializable<?, ?> abilityInstance : abilities) {
            ResourceKey<ConfiguredSkill<?, ?>> skill = abilityInstance.data().getConfiguredAbility().config().getData().parentSkill().getKey();
            skills.add(skill);
        }

        // Initialize item displays
        HolderLookup.RegistryLookup<ConfiguredSkill<?, ?>> holderGetter = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL);
        int index = 0;
        for (ResourceKey<ConfiguredSkill<?, ?>> skill : skills) {
            itemDisplays.computeIfAbsent(index, k -> {
                        // Create item display for the skill
                        SkillIconScreenElement display = new SkillIconScreenElement(screen, Settings.DEFAULT.withAnchor(Anchor.CENTER), holderGetter.getOrThrow(skill), SkillIconsScreenElement.BASE_SCALE);
                        // Parent to this element
                        display.setParent(this);
                        // Add click listener
                        display.addClickListener(this::onElementClicked);
                        display.addMouseEnterListener(this::onElementHovered);
                        display.addMouseExitListener((x, y, element) -> onElementHovered(x, y, null));

                        return display;
                    });
            index++;
        }

        this.skills.clear();
        this.skills.addAll(skills);
    }

    /**
     * Handle click event on a skill icon
     * @param x The clicked position
     * @param y The clicked position
     * @param element The clicked element (should be an ItemDisplay) for the skill icon
     */
    private void onElementClicked(int x, int y, IRenderableScreenElement element) {
        if (element instanceof SkillIconScreenElement skillIconScreenElement) {
            if (selectedItem == element) {
                selectedItem = null;

                selectedTimestamp = -1;
                deselectedTimestamp = ClientTickHandler.total();
            } else {
                selectedItem = skillIconScreenElement;

                selectedTimestamp = ClientTickHandler.total();
                deselectedTimestamp = -1;
            }

            onIconSelected.accept(selectedItem);
        }
    }

    private void onElementHovered(int x, int y, IRenderableScreenElement element) {
        if (element instanceof SkillIconScreenElement skillIconScreenElement) {
            hoveredItem = skillIconScreenElement;
        } else {
            hoveredItem = null;
        }
    }

    private void tickWheelRotation(float partialTick, boolean anyHovered) {
        // Update wheel rotation
        wheelRotationSpeed = RUtil.lerp(wheelRotationSpeed, anyHovered ? 0F : 1F, partialTick * 0.05F);
        wheelRotation += wheelRotationSpeed * partialTick;
    }

    private void updatePositions() {
        float radius = getRadius();

        // Distribute items in a circle, excluding the selected item.
        Vector3f[] pointsInWheel = RUtil.distributePointsOnCircle(
                selectedItem == null ? itemDisplays.size() : itemDisplays.size() - 1,
                new Vector3f(0, 0, 1F),
                new Vector3f(0, 0, 0),
                (float) Math.toRadians(wheelRotation),
                radius,
                0);
        Arrays.stream(pointsInWheel).forEach(point -> point.add(new Vector3f(radius, radius, 0)));

        Rectangle2D bounds = getGlobalBounds();
        int index = 0;
        for (SkillIconScreenElement display : itemDisplays.values()) {
            if (display.isSelected) {
                Rectangle2D itemBounds = display.getGlobalBounds();
                display.setTargetPosition(new Vector3f((float) (bounds.getWidth() / 2F), (float) (bounds.getHeight() / 2F), 0), Scope.LOCAL, false);
            } else if (index < pointsInWheel.length) {
                display.setTargetPosition(pointsInWheel[index], Scope.LOCAL, false);
                index++;
            }
        }
    }

    public float getRadius() {
        float baseRadius = Math.min(this.screen.width, this.screen.height);
        return baseRadius * (deselectedTimestamp != -1 ?
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_WHEEL_DESELECTED).getScale().evaluate(getSelectAnimationProgress()) :
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_WHEEL_SELECTED).getScale().evaluate(getSelectAnimationProgress()));
    }

    /**
     * Get the global position offset for GUI elements
     * @param additionalOffset Additional offset to apply (X: 1F = 1 screen width, Y: 1F = 1 screen height)
     * @return
     */
    private Vector3f getGlobalPositionOffset(Vector3f additionalOffset) {
        Vector3f offset = new Vector3f(deselectedTimestamp != -1 ?
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_WHEEL_DESELECTED).getPosition().evaluate(getSelectAnimationProgress()) :
                SpatialAnimations.get(SpatialAnimations.SKILL_ICON_WHEEL_SELECTED).getPosition().evaluate(getSelectAnimationProgress())
        );
        offset.add(additionalOffset);
        offset.mul(this.screen.width, this.screen.width, 1F);
        return offset;
    }

    private float getSelectAnimationProgress() {
        if (selectedTimestamp != -1) {
            return Math.clamp((ClientTickHandler.total() - selectedTimestamp) / HOVER_DURATION, 0, 1);
        } else if (deselectedTimestamp != -1) {
            return Math.clamp((ClientTickHandler.total() - deselectedTimestamp) / HOVER_DURATION, 0, 1);
        }
        return 0;
    }

    private Collection<AbilityInstanceSerializable<?, ?>> getAbilities() {
        if (this.screen.getMinecraft().player == null) {
            return Collections.emptyList();
        }
        return SkillsData.getPlayerData(this.screen.getMinecraft().player).unlockedAbilities().values().stream().flatMap(List::stream).toList();
    }
}

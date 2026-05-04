package grill24.potionsplus.render.animation;

import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.List;

public class WheelItemActivationAnimation extends ItemActivationAnimation {
    private final List<ItemStack> wheelItems;
    private int winningItemStackIndex;

    private boolean hasSpawnedWinnerParticles;

    public WheelItemActivationAnimation(List<ItemStack> itemStacks) {
        super(getDuration());

        this.wheelItems = itemStacks;
        this.winningItemStackIndex = !this.wheelItems.isEmpty() ? Minecraft.getInstance().level.getRandom().nextInt(this.wheelItems.size()) : -1;

        this.hasSpawnedWinnerParticles = false;
    }

    private static int getDuration() {
        return (int) Math.max(
                Math.max(SpatialAnimations.get(SpatialAnimations.WHEEL_SPIN).getDuration(),
                        SpatialAnimations.get(SpatialAnimations.WHEEL_POSITION_OFFSET).getDuration()),
                Math.max(SpatialAnimations.get(SpatialAnimations.WHEEL_WINNER).getDuration(),
                        SpatialAnimations.get(SpatialAnimations.WHEEL_LOSERS).getDuration()));
    }

    public static WheelItemActivationAnimation withRandomWinner(List<ItemStack> stacks) {
        return new WheelItemActivationAnimation(stacks);
    }

    public static WheelItemActivationAnimation withWinner(List<ItemStack> stacks, int winningItemStackIndex) {
        WheelItemActivationAnimation animation = new WheelItemActivationAnimation(stacks);
        animation.winningItemStackIndex = winningItemStackIndex;
        return animation;
    }

    @Override
    public void render(Minecraft minecraft, GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick) {
        if (this.itemActivationTicksRemaining > 0) {
            float elapsedTicks = this.itemActivationMaxTicks - this.itemActivationTicksRemaining + partialTick;
            float scale = 150.0F;

            spawnParticles(minecraft);

            float rotationEnd = (float) -((Math.PI * 2 / this.wheelItems.size() * winningItemStackIndex) + (Math.PI * 12));
            Vector3f[] points = RUtil.distributePointsOnCircle(
                    this.wheelItems.size(),
                    new Vector3f(0, 1F, 0),
                    new Vector3f(),
                    SpatialAnimations.get(SpatialAnimations.WHEEL_SPIN).getRotation().evaluate(elapsedTicks).x() * rotationEnd,
                    scale,
                    0);

            Vector3f positionOffset = new Vector3f(SpatialAnimations.get(SpatialAnimations.WHEEL_POSITION_OFFSET).getPosition().evaluate(elapsedTicks));
            positionOffset.mul(scale);

            for (int i = 0; i < this.wheelItems.size(); i++) {
                int itemX = GuiGraphicsExtractor.guiWidth() / 2 + (int)(points[i].x + positionOffset.x());
                int itemY = GuiGraphicsExtractor.guiHeight() / 2 + (int)(points[i].y + positionOffset.y());
                GuiGraphicsExtractor.item(this.wheelItems.get(i), itemX, itemY);
            }
        }
    }

    public void spawnParticles(Minecraft minecraft) {
        float elapsedTicks = this.itemActivationMaxTicks - this.itemActivationTicksRemaining;
        if (minecraft.player != null && !hasSpawnedWinnerParticles && elapsedTicks >= 180) {
            minecraft.particleEngine.createTrackingEmitter(
                    minecraft.player,
                    ParticleTypes.TOTEM_OF_UNDYING,
                    30
            );
            hasSpawnedWinnerParticles = true;

            minecraft.player.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
        }
    }
}

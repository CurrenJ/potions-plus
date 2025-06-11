package grill24.potionsplus.render.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class ItemTossupAnimation extends ItemActivationAnimation {
    public static class PhysicsItem {
        public ItemStack stack;

        public Vector3f position;
        public Vector3f velocity;

        public Vector3f rotation;
        public Vector3f rotationalVelocity;

        public PhysicsItem(ItemStack stack, Vector3f position, Vector3f velocity, Vector3f rotationalVelocity) {
            this.stack = stack;
            this.position = position;
            this.velocity = velocity;
            this.rotation = new Vector3f();
            this.rotationalVelocity = rotationalVelocity;
        }

        public PhysicsItem(ItemStack stack, Vector3f position) {
            this.stack = stack;
            this.position = position;
            this.velocity = new Vector3f();
            this.rotation = new Vector3f();
            this.rotationalVelocity = new Vector3f();
        }

        public void addVelocity(Vector3f velocity) {
            this.velocity.add(velocity);
        }

        public void update(float deltaTime, float timeScale) {
            this.velocity.add(new Vector3f(0, 0.01F, 0).mul(deltaTime * timeScale));

            this.velocity.add(new Vector3f(this.velocity).mul(-0.01F * deltaTime * timeScale));
            this.position.add(new Vector3f(this.velocity).mul(deltaTime * timeScale));

            this.rotationalVelocity.add(new Vector3f(this.rotationalVelocity).mul(-0.01F * deltaTime * timeScale));
            this.rotation.add(new Vector3f(this.rotationalVelocity).mul(deltaTime * timeScale));
        }

        public void update(float deltaTime) {
            this.update(deltaTime, 1.0F);
        }
    }

    public List<Pair<Integer, ItemStack>> stacks;
    public List<PhysicsItem> physicsItems;

    public final int ticksPerStack;
    public final float timeScale;

    public ItemTossupAnimation(int ticksPerStack, List<ItemStack> stacks) {
        this(ticksPerStack, stacks, 1.0F);
    }

    public ItemTossupAnimation(int ticksPerStack, List<ItemStack> stacks, float timeScale) {
        super(ticksPerStack * stacks.size() + 60);

        this.ticksPerStack = ticksPerStack;
        this.timeScale = timeScale;
        // Map with index
        this.stacks = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            this.stacks.add(new Pair<>(i * ticksPerStack, stacks.get(i)));
        }
        this.physicsItems = new ArrayList<>();
    }

    public ItemTossupAnimation(List<ItemStack> stacks) {
        this(20, stacks);
    }

    public static ItemTossupAnimation withItems(List<ItemStack> stacks) {
        return new ItemTossupAnimation(stacks);
    }

    public static ItemTossupAnimation withItems(List<ItemStack> stacks, float timeScale) {
        return new ItemTossupAnimation(20, stacks, timeScale);
    }

    public static ItemTossupAnimation withItems(List<ItemStack> stacks, int ticksPerStack) {
        return new ItemTossupAnimation(ticksPerStack, stacks);
    }

    public static ItemTossupAnimation withItems(List<ItemStack> stacks, int ticksPerStack, float timeScale) {
        return new ItemTossupAnimation(ticksPerStack, stacks, timeScale);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.stacks.isEmpty()) {
            float elapsedTicks = this.itemActivationMaxTicks - this.itemActivationTicksRemaining;
            List<Integer> toRemove = new ArrayList<>();
            for (Pair<Integer, ItemStack> pair : this.stacks) {
                if (elapsedTicks >= pair.getA()) {
                    Vector3f position = new Vector3f(0, 1F, 0);
                    Vector3f velocity = new Vector3f((float) Math.random() * 0.1F - 0.05F, (float) (-0.125F - Math.random() * 0.05F), (float) Math.random() * 0.1F - 0.05F);
                    Vector3f rotationalVelocity = new Vector3f((float) Math.random() * 0.1F - 0.05F, (float) Math.random() * 0.1F - 0.05F, (float) Math.random() * 0.1F - 0.05F);

                    this.physicsItems.add(new PhysicsItem(pair.getB(), position, velocity, rotationalVelocity));
                    toRemove.add(this.stacks.indexOf(pair));
                }
            }
            for (int i : toRemove) {
                this.stacks.remove(i);
            }
        }
    }

    @Override
    public void render(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick) {
        if (this.itemActivationTicksRemaining > 0) {
            float scale = 150.0F;

            for (PhysicsItem physicsItem : this.physicsItems) {
                physicsItem.update(partialTick, this.timeScale);
            }

            // Render physics items
            for (PhysicsItem physicsItem : this.physicsItems) {
                PoseStack poseStack = new PoseStack();
                poseStack.pushPose();
                poseStack.translate(guiGraphics.guiWidth() / 2.0F, guiGraphics.guiHeight() / 2.0F, -50.0F);
                poseStack.translate(physicsItem.position.x * scale, physicsItem.position.y * scale, physicsItem.position.z * scale);
                // Assuming parabolic curve, rotate the item to face the direction of the velocity
//                poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(physicsItem.velocity.x, physicsItem.velocity.z))));
//                poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.atan2(physicsItem.velocity.y, Math.sqrt(physicsItem.velocity.x * physicsItem.velocity.x + physicsItem.velocity.z * physicsItem.velocity.z)))));
                poseStack.mulPose(Axis.YP.rotationDegrees(physicsItem.rotation.x * 180));
                poseStack.mulPose(Axis.XP.rotationDegrees(physicsItem.rotation.y * 180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(physicsItem.rotation.z * 360));
                poseStack.scale(scale, scale, scale);

                guiGraphics.drawSpecial(bufferSource -> minecraft.getItemRenderer().renderStatic(
                        physicsItem.stack,
                        ItemDisplayContext.FIXED,
                        15728880,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        bufferSource,
                        minecraft.level,
                        0
                ));

                poseStack.popPose();
            }
        }
    }
}

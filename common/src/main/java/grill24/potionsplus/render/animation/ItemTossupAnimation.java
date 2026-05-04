package grill24.potionsplus.render.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void render(Minecraft minecraft, GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick) {
        if (this.itemActivationTicksRemaining > 0) {
            float scale = 150.0F;

            for (PhysicsItem physicsItem : this.physicsItems) {
                physicsItem.update(partialTick, this.timeScale);
            }

            // Render physics items
            for (PhysicsItem physicsItem : this.physicsItems) {
                int itemX = GuiGraphicsExtractor.guiWidth() / 2 + (int)(physicsItem.position.x * scale);
                int itemY = GuiGraphicsExtractor.guiHeight() / 2 + (int)(physicsItem.position.y * scale);
                GuiGraphicsExtractor.item(physicsItem.stack, itemX, itemY);
            }
        }
    }
}

package grill24.potionsplus.misc;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.network.ServerboundEndFishingMinigame;
import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.FishingMinigameAnimation;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;

/**
 * Represents an instance of the fishing game that runs client-side.
 * Server keeps track of whether an instance is running via the {@link FishingGamePlayerAttachment}.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class LocalFishingGame {
    private static LocalFishingGame instance = null;

    private float bobberPosition;
    private final float maxBobberPosition;
    private float bobberSpeed;
    private final float bobberSize;

    private float captureProgress;
    private boolean isCapturing;

    private float fishPosition = 0.5F;
    private float targetFishPosition = 0.5F;
    private final float fishSize = 4.0F / 32.0F;

    private int nextFishMovementTimestamp;
    private float fishMovementSpeed;

    private boolean isGameOver;
    private boolean isCaptured;
    private int gameOverTimestamp;
    private float difficulty;

    public LocalFishingGame(float bobberSize, float difficulty) {
        this.bobberPosition = 0.0F;
        this.bobberSpeed = 0.0F;
        this.bobberSize = bobberSize;
        this.maxBobberPosition = 1.0F - bobberSize;

        this.isGameOver = false;
        this.isCaptured = false;
        this.gameOverTimestamp = -1;
        this.difficulty = difficulty;

        captureProgress = 0.5F;
        randomizeFishPosition();

        ((IGameRendererMixin) Minecraft.getInstance().gameRenderer).potions_plus$displayItemActivation(new FishingMinigameAnimation(this));
    }

    private void randomizeFishPosition() {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        RandomSource random = Minecraft.getInstance().level.getRandom();
        Float[] fishPositions = new Float[Math.max(1, (int) (3 * difficulty))];
        for (int i = 0; i < fishPositions.length; i++) {
            fishPositions[i] = random.nextFloat();
        }
        // Pick fishPosition that is furthest away from the current fishPosition
        this.targetFishPosition = Arrays.stream(fishPositions).max((a, b) -> {
            float distA = Math.abs(a - this.fishPosition);
            float distB = Math.abs(b - this.fishPosition);
            return Float.compare(distA, distB);
        }).get();
        this.fishMovementSpeed = random.nextFloat() * 0.05F + 0.05F;

        this.nextFishMovementTimestamp = ClientTickHandler.ticksInGame + Minecraft.getInstance().level.getRandom().nextInt((int) (30 / this.difficulty));
    }

    private void onTick() {
        if(!isOver()) {
            final float gravity = 0.007F;

            // Move the bobber by the speed
            this.bobberPosition += this.bobberSpeed;
            // Bounce the bobber if it goes out of bounds
            if (this.bobberPosition < 0.0F || this.bobberPosition > maxBobberPosition) {
                this.bobberSpeed *= -0.7F;
                if (Math.abs(this.bobberSpeed) < 0.03F) {
                    this.bobberSpeed = 0.0F;
                }
            }
            // Clamp the bobber position
            this.bobberPosition = Math.clamp(this.bobberPosition, 0.0F, maxBobberPosition);
            // Apply gravity
            this.bobberSpeed -= gravity;
            // Slow down the bobber
            this.bobberSpeed *= 0.9F;

            // Check if the bobber is in the fish capture area
            final float fishLowerBound = fishPosition - fishSize / 2F;
            final float fishUpperBound = fishPosition + fishSize / 2F;
            final float bobberBarLowerBound = bobberPosition;
            final float bobberBarUpperBound = bobberPosition + bobberSize;
            if (bobberBarUpperBound >= fishLowerBound && bobberBarLowerBound <= fishUpperBound) {
                this.captureProgress += 0.01F / difficulty;
                this.isCapturing = true;
                if (this.captureProgress >= 1.0F) {
                    this.isGameOver = true;
                    this.isCaptured = true;
                    this.gameOverTimestamp = ClientTickHandler.ticksInGame;

                    PacketDistributor.sendToServer(new ServerboundEndFishingMinigame(ServerboundEndFishingMinigame.Result.SUCCESS));
                }
            } else {
                this.captureProgress -= 0.01F * difficulty;
                if (this.captureProgress <= 0.0F) {
                    this.isGameOver = true;
                    this.isCaptured = false;
                    this.gameOverTimestamp = ClientTickHandler.ticksInGame;

                    PacketDistributor.sendToServer(new ServerboundEndFishingMinigame(ServerboundEndFishingMinigame.Result.FAILURE));
                }
                this.isCapturing = false;
            }
            this.captureProgress = Math.clamp(this.captureProgress, 0.0F, 1.0F);

            // Move the fish
            this.fishPosition = predictFishPosition(1F);
            if (ClientTickHandler.ticksInGame >= this.nextFishMovementTimestamp) {
                randomizeFishPosition();
            }

            PotionsPlus.LOGGER.info("Capture progress: " + this.captureProgress);
        }
    }

    public boolean isCapturing() {
        return this.isCapturing;
    }

    public float setFishPosition(float targetFishPosition) {
        return this.targetFishPosition = targetFishPosition;
    }

    public float predictPosition(float partialTick) {
        if (isOver()) {
            partialTick = 1;
        }

        float predictedPosition = this.bobberPosition + this.bobberSpeed * partialTick;
        predictedPosition = Math.clamp(predictedPosition, 0.0F, maxBobberPosition);
        return predictedPosition / maxBobberPosition;
    }

    public float predictFishPosition(float partialTick) {
        if (isOver()) {
            partialTick = 1;
        }

        return RUtil.lerp(fishPosition, targetFishPosition, partialTick * fishMovementSpeed);
    }

    public void useRod() {
        if (isOver()) {
            return;
        }

        this.bobberSpeed += 0.05F;
    }

    public boolean isOver() {
        return this.isGameOver;
    }

    public int getGameOverTimestamp() {
        return this.gameOverTimestamp;
    }

    public float getBobberPosition() {
        return this.bobberPosition / maxBobberPosition;
    }

    public float getBobberSize() {
        return this.bobberSize;
    }

    public float getTargetPosition() {
        return this.fishPosition;
    }

    public float getCaptureProgress() {
        return this.captureProgress;
    }

    public boolean isCaptured() {
        return this.isCaptured;
    }

    public static void tryTick() {
        if (instance != null) {
            instance.onTick();
        }
    }

    public static void newGame() {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        instance = new LocalFishingGame(9.0F / 28.0F, Minecraft.getInstance().level.getRandom().nextFloat() * 2.0F);
    }

    @SubscribeEvent
    public static void onGameTick(ClientTickEvent.Pre event) {
        tryTick();
    }

    public float getFishSize() {
        return this.fishSize;
    }
}

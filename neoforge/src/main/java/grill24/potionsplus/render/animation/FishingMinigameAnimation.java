package grill24.potionsplus.render.animation;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.misc.LocalFishingGame;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class FishingMinigameAnimation extends ItemActivationAnimation {
    private LocalFishingGame localFishingGame;
    private static final int BRIGHTNESS = 15728880;
    private static final float Z_OFFSET = -1 / 32F;

    public FishingMinigameAnimation(LocalFishingGame localFishingGame) {
        super(1000);

        this.localFishingGame = localFishingGame;

        this.animationStartTimestamp = ClientTickHandler.ticksInGame;
        this.rotationSpeed = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);

        setRandomRotationSpeed(Minecraft.getInstance());
    }

    private final int animationStartTimestamp;
    private Vector3f rotationSpeed;
    private Vector3f rotation;
    private int nextRotationChangeTimestamp;

    @Override
    public void render(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick) {
        if (minecraft.player == null) {
            return;
        }

        final float scaleInProgress = RUtil.getProgress(0, 10F, this.animationStartTimestamp, ClientTickHandler.total());
        final float size = Math.min(guiGraphics.guiWidth(), guiGraphics.guiHeight()) / 2.0F * SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate(scaleInProgress);
        Vector3f offset = new Vector3f(-0.375F /* 12 / 32 pixel offset to center png */ , 0, 0);

        final float bottomPadding = 0.0625F;
        final float topPadding = 0.0625F;

        final float barSize = 9.0F / 32.0F; /* 9 / 32 pixel size */
        final float maxBarOffset = 1.0F - bottomPadding - topPadding - barSize;
        final float barOffset = localFishingGame.predictPosition(partialTick) * maxBarOffset;

        updateRotation(minecraft, partialTick);

        PoseStack barElementsTransform = new PoseStack();
        barElementsTransform.translate(guiGraphics.guiWidth() / 2.0F, guiGraphics.guiHeight() / 2.0F, -50.0F);
        barElementsTransform.translate(-guiGraphics.guiWidth() / 8.0F, 0, 0);
        barElementsTransform.mulPose(RUtil.rotate(new Vector3f(0, 180, 0)));
        barElementsTransform.scale(size, -size, size);

        barElementsTransform.pushPose();
        if (localFishingGame.isOver()) {
            float shrinkOutProgress = (ClientTickHandler.ticksInGame + partialTick - localFishingGame.getGameOverTimestamp()) / 10F;
            float scale = SpatialAnimations.get(SpatialAnimations.SCALE_OUT_QUAD).getScale().evaluate(shrinkOutProgress);
            barElementsTransform.scale(scale, scale, scale);
        }
        barElementsTransform.translate(offset.x(), offset.y(), offset.z());
        barElementsTransform.mulPose(RUtil.rotate(this.rotation));
        minecraft.getItemRenderer().renderStatic(
                new ItemStack(Items.GENERIC_ICON, 21), // 21 is the fishing game bar icon
                ItemDisplayContext.FIXED,
                BRIGHTNESS,
                OverlayTexture.NO_OVERLAY,
                barElementsTransform,
                guiGraphics.bufferSource(),
                minecraft.level,
                0
        );

        barElementsTransform.pushPose();
        barElementsTransform.translate(0, barOffset, Z_OFFSET);
        minecraft.getItemRenderer().renderStatic(
                new ItemStack(Items.GENERIC_ICON, 22), // 22 is the capture bar icon
                ItemDisplayContext.FIXED,
                BRIGHTNESS,
                OverlayTexture.NO_OVERLAY,
                barElementsTransform,
                guiGraphics.bufferSource(),
                minecraft.level,
                0
        );
        barElementsTransform.popPose();

        barElementsTransform.popPose();


        // Fish Reward Stack Icon
        barElementsTransform.pushPose();
        final float barInteriorWidth = 0.125F;
        Vector3f rotationOffset = new Vector3f();

        ItemStack fishReward = new ItemStack(Items.GENERIC_ICON, 12);
        float winnerEnlargement = 1;
        if (localFishingGame.isOver()) {
            if (localFishingGame.isCaptured()) {
                int gameOverTimestamp = localFishingGame.getGameOverTimestamp();
                float gameOverAnimationProgress = ClientTickHandler.ticksInGame + partialTick - gameOverTimestamp;
                rotationOffset.add(SpatialAnimations.get(SpatialAnimations.FISHING_REWARD_WIN_SPIN).getRotation().evaluate(gameOverAnimationProgress));
                if (gameOverAnimationProgress > 7 && minecraft.player.hasData(DataAttachments.FISHING_GAME_DATA)) {
                    FishingGamePlayerAttachment fishingGamePlayerAttachment = minecraft.player.getData(DataAttachments.FISHING_GAME_DATA);
                    fishReward = fishingGamePlayerAttachment.fishReward();
                }
                winnerEnlargement = SpatialAnimations.get(SpatialAnimations.FISHING_REWARD_WIN_SPIN).getScale().evaluate(gameOverAnimationProgress);

                if (gameOverAnimationProgress > SpatialAnimations.get(SpatialAnimations.FISHING_REWARD_WIN_SPIN).getDuration()) {
                    this.itemActivationTicksRemaining = 0;
                }
            } else {
                float shrinkOutProgress = (ClientTickHandler.ticksInGame + partialTick - localFishingGame.getGameOverTimestamp()) / 10F;
                float scale = SpatialAnimations.get(SpatialAnimations.SCALE_OUT_QUAD).getScale().evaluate(shrinkOutProgress);

                if (shrinkOutProgress > SpatialAnimations.get(SpatialAnimations.SCALE_OUT_QUAD).getDuration()) {
                    this.itemActivationTicksRemaining = 0;
                }
                winnerEnlargement = scale;
            }

        }
        barElementsTransform.mulPose(RUtil.rotate(rotationOffset.add(this.rotation)));
        barElementsTransform.pushPose();
        // subtract (14 / 32) because item renders in center of bar when using existing transforms.
        // align to bottom of inner bar bc fishing game data assumes 0F position is bottom of bar
        barElementsTransform.translate(0, (localFishingGame.predictFishPosition(partialTick) - (14F / 32F)) * maxBarOffset, Z_OFFSET * 3);
        final float iconSize = localFishingGame.getCaptureProgress() * barInteriorWidth * winnerEnlargement;

        barElementsTransform.pushPose();
        barElementsTransform.scale(iconSize, iconSize, iconSize);
        minecraft.getItemRenderer().renderStatic(
                fishReward,
                ItemDisplayContext.FIXED,
                BRIGHTNESS,
                OverlayTexture.NO_OVERLAY,
                barElementsTransform,
                guiGraphics.bufferSource(),
                minecraft.level,
                0
        );
        barElementsTransform.popPose();

        barElementsTransform.pushPose();
        barElementsTransform.translate(0, 0, Z_OFFSET);
        final float frameIconSize = localFishingGame.getFishSize() * winnerEnlargement;
        barElementsTransform.scale(frameIconSize, frameIconSize, frameIconSize);
        float rotation = 0;
        if(!localFishingGame.isOver() && localFishingGame.isCapturing()) {
            rotation = (float) (Math.sin((ClientTickHandler.ticksInGame + partialTick) * 2) * 4);
        }
        barElementsTransform.mulPose(RUtil.rotate(new Vector3f(rotation, rotation, rotation)));

        ItemStack frame = new ItemStack(Items.GENERIC_ICON, 23);
        if (minecraft.player.hasData(DataAttachments.FISHING_GAME_DATA)) {
            FishingGamePlayerAttachment fishingGamePlayerAttachment = minecraft.player.getData(DataAttachments.FISHING_GAME_DATA);
            frame = fishingGamePlayerAttachment.frameType();
        }
        minecraft.getItemRenderer().renderStatic(
                frame,
                ItemDisplayContext.FIXED,
                BRIGHTNESS,
                OverlayTexture.NO_OVERLAY,
                barElementsTransform,
                guiGraphics.bufferSource(),
                minecraft.level,
                0
        );
        barElementsTransform.popPose();
    }

    private void updateRotation(Minecraft minecraft, float partialTick) {
        this.rotation.add(this.rotationSpeed.mul( 1 - (0.02F * partialTick)));
        final float maxRot = 5;
        this.rotation.x = Math.min(maxRot, Math.max(-maxRot, this.rotation.x));
        this.rotation.y = Math.min(maxRot, Math.max(-maxRot, this.rotation.y));
        this.rotation.z = Math.min(maxRot, Math.max(-maxRot, this.rotation.z));

        if (this.nextRotationChangeTimestamp <= ClientTickHandler.ticksInGame) {
            setRandomRotationSpeed(minecraft);
        }
    }

    private void setRandomRotationSpeed(Minecraft minecraft) {
        if (minecraft.level == null) {
            return;
        }

        RandomSource random = minecraft.level.getRandom();
        this.rotationSpeed = new Vector3f(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).mul(random.nextFloat() * 0.25F);
        this.nextRotationChangeTimestamp = ClientTickHandler.ticksInGame + 20 + random.nextInt(60);
    }

    public LocalFishingGame getGame() {
        return localFishingGame;
    }
}

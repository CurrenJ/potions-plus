package grill24.potionsplus.blockentity;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.extension.IMobEffectInstanceExtension;
import grill24.potionsplus.network.ClientboundBlockEntityCraftRecipePacket;
import grill24.potionsplus.utility.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PotionBeaconBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer, ICraftingBlockEntity {
    public List<MobEffectInstance> effects = new ArrayList<>();

    public HerbalistsLecternSounds sounds;
    public RendererData rendererData = new RendererData();

    public static class RendererData {
        public class ItemParticle {
            public Vector3d position;
            public Vector3d velocity;
            public Vector3f rotation;
            public Vector3f rotationalVelocity;
            public float age;
            public float lifetime;
            public float scale;
        }

        public List<ItemParticle> itemParticles = new ArrayList<>();

        private int timeItemPlaced;
        public Vector3f itemRestingRotation = new Vector3f(90, 0, 0);
        public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, (3 / 16F), 0.5);
        private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);
        public Vector3d localPlayerPositionRelativeToBlockEntity = new Vector3d(0, 0, 0);

        public int innerBlockShownTimestamp;
        public int effectDurationWhenShown;

        public RendererData() {
        }
    }

    public PotionBeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Blocks.POTION_BEACON_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    protected int getSlots() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    public int getTimeItemPlaced() {
        return rendererData.timeItemPlaced;
    }

    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(rendererData.itemAnimationStartingPosRelativeToBlockOrigin.x, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.y, rendererData.itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return RendererData.itemRestingPositionTranslation;
    }

    @Override
    public Vector3f getRestingRotation() {
        return rendererData.itemRestingRotation;
    }

    @Override
    public int getInputAnimationDuration() {
        return 20;
    }

    /**
     * Hijack block entity craft packet to set the timestamp of the inner block being shown - lazy...
     *
     * @param maxEffectDuration
     */
    @Override
    public void craft(int maxEffectDuration) {
        this.rendererData.innerBlockShownTimestamp = (int) ClientTickHandler.total();
        this.rendererData.effectDurationWhenShown = maxEffectDuration;
    }

    public Vector3f getLocalPlayerRelativePosition() {
        return new Vector3f((float) rendererData.localPlayerPositionRelativeToBlockEntity.x, (float) rendererData.localPlayerPositionRelativeToBlockEntity.y, (float) rendererData.localPlayerPositionRelativeToBlockEntity.z);
    }

    private static final Set<Holder<Potion>> HIDDEN_POTIONS = Set.of(
            Potions.THICK,
            Potions.MUNDANE
    );

    public void onPlayerInsertItem(Player player) {
        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        rendererData.itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        rendererData.timeItemPlaced = ((int) ClientTickHandler.total());
        rendererData.itemRestingRotation = new Vector3f(90, 0, Utility.getHorizontalDirectionTowardsBlock(player.blockPosition(), this.getBlockPos()).toYRot());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PotionBeaconBlockEntity blockEntity) {
        if (blockEntity.effects.isEmpty() && !blockEntity.getItem(0).isEmpty()) {
            ItemStack stack = blockEntity.getItem(0);
            if (stack.has(DataComponents.POTION_CONTENTS)) {
                PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
                PUtil.getAllEffects(potionContents).stream().map(MobEffectInstance::new).forEach(blockEntity.effects::add);
            }
            stack.shrink(1);
            blockEntity.setItem(0, stack);
            blockEntity.setChanged();

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, blockEntity.worldPosition, SoundEvents.GENERIC_DRINK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                updateLitStateServer(serverLevel, blockEntity);
            }

            // Spawn potion particles while active... TODO
        }

        if (level.isClientSide && blockEntity.getBlockState().getValue(PotionBeaconBlock.LIT)) {
            float timeSincePlaced = ClientTickHandler.total() - blockEntity.rendererData.innerBlockShownTimestamp;
            float durationFactor = Math.clamp((blockEntity.rendererData.effectDurationWhenShown - timeSincePlaced) / 3600, 0, 1);
            int tickInterval = (int) RUtil.lerp(1, 4, durationFactor);
            if (ClientTickHandler.ticksInGame % tickInterval == 0) {
                blockEntity.rendererData.itemParticles.removeIf(itemParticle -> itemParticle.age > itemParticle.lifetime);

                for (int i = 0; i < tickInterval; i++) {
                    double x = 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                    double y = 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                    double z = 0.5 + (level.random.nextDouble() - 0.5) * 0.5;

                    // Speed is away from center point (0.5, 0.5)
                    double speed = 0.1 * level.random.nextDouble();
                    double dx = (x - 0.5) * speed;
                    double dy = (y - 0.5) * speed;
                    double dz = (z - 0.5) * speed;

                    // Rotation
                    Vector3f rotationDegrees = new Vector3f((float) (level.random.nextDouble() * 360), (float) (level.random.nextDouble() * 360), (float) (level.random.nextDouble() * 360));
                    final int ROTATIONAL_VELOCITY_STD_DEV = 5;
                    Vector3f rotationalVelocity = new Vector3f((float) Utility.nextGaussian(0, ROTATIONAL_VELOCITY_STD_DEV, level.random), (float) Utility.nextGaussian(0, ROTATIONAL_VELOCITY_STD_DEV, level.random), (float) Utility.nextGaussian(0, ROTATIONAL_VELOCITY_STD_DEV, level.random));

                    RendererData.ItemParticle itemParticle = blockEntity.rendererData.new ItemParticle();
                    itemParticle.position = new Vector3d(x, y, z);
                    itemParticle.velocity = new Vector3d(dx, dy, dz);
                    itemParticle.rotation = rotationDegrees;
                    itemParticle.rotationalVelocity = rotationalVelocity;
                    itemParticle.lifetime = (float) Utility.nextGaussian(100, 20, level.random);
                    itemParticle.scale = (float) RUtil.lerp(0, Utility.nextGaussian(0.20, 0.1, level.random), durationFactor);

                    blockEntity.rendererData.itemParticles.add(itemParticle);
                }
            }
        }

        // Apply effects
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            final int TICK_INTERVAL = 60;
            List<MobEffectInstance> toRemove = new ArrayList<>();
            if (ServerTickHandler.ticksInGame % TICK_INTERVAL == 0) {
                for (MobEffectInstance effect : blockEntity.effects) {
                    if (effect instanceof IMobEffectInstanceExtension mobEffectInstance) {
                        int effectDurationToApply = Math.min(TICK_INTERVAL, effect.getDuration());
                        int remainingDuration = Math.max(0, effect.getDuration() - effectDurationToApply);
                        mobEffectInstance.potions_plus$setDuration(remainingDuration);

                        final int finalEffectDurationToApply = effect.is(MobEffects.NIGHT_VISION) ? 300 : effectDurationToApply;
                        level.getEntitiesOfClass(Player.class, new AABB(blockEntity.worldPosition).inflate(16.0)).forEach(player -> {
                            MobEffectInstance effectInstance = new MobEffectInstance(effect);
                            IMobEffectInstanceExtension effectToApply = (IMobEffectInstanceExtension) effectInstance;
                            effectToApply.potions_plus$setDuration(finalEffectDurationToApply);
                            player.addEffect(effectInstance);
                        });

                        if (effect.getDuration() == 0) {
                            toRemove.add(effect);
                        }
                    }
                }

                blockEntity.effects.removeAll(toRemove);
                if (!toRemove.isEmpty()) {
                    updateLitStateServer(serverLevel, blockEntity);
                }
            }
        }
    }

    private static void updateLitStateServer(ServerLevel serverLevel, PotionBeaconBlockEntity blockEntity) {
        serverLevel.setBlockAndUpdate(blockEntity.worldPosition, blockEntity.getBlockState().setValue(PotionBeaconBlock.LIT, !blockEntity.effects.isEmpty()));
        final int maxDuration = blockEntity.effects.stream().mapToInt(MobEffectInstance::getDuration).max().orElse(0);
        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, serverLevel.getChunkAt(blockEntity.worldPosition).getPos(), new ClientboundBlockEntityCraftRecipePacket(blockEntity.worldPosition, maxDuration));
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public void writePacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.writePacketNbt(tag, registryAccess);

        DataResult<JsonElement> result = MobEffectInstance.CODEC.listOf().encodeStart(JsonOps.INSTANCE, effects);
        String jsonString = result.result().orElse(JsonOps.INSTANCE.empty()).toString();

        tag.putString("Effects", jsonString);
    }

    @Override
    public void readPacketNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        super.readPacketNbt(tag, registryAccess);

        this.effects.clear();

        if (tag.contains("Effects")) {
            String jsonString = tag.getStringOr("Effects", "");
            JsonElement jsonElement = JsonOps.INSTANCE.createString("");
            DataResult<List<MobEffectInstance>> result = MobEffectInstance.CODEC.listOf().parse(JsonOps.INSTANCE, jsonElement);
            result.result().ifPresent(effects -> this.effects.addAll(effects));
        }
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }
}
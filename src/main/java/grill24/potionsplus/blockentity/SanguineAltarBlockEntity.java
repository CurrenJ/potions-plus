package grill24.potionsplus.blockentity;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import grill24.potionsplus.core.*;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.PotionsPlusPacketHandler;
import grill24.potionsplus.network.SanguineAltarConversionProgressPacket;
import grill24.potionsplus.network.SanguineAltarConversionStatePacket;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class SanguineAltarBlockEntity extends InventoryBlockEntity implements ISingleStackDisplayer {
    public static final int CONVERSION_TICKS = 200;
    public static final float CONVERSION_DURATION_HERTZ = 1 / ((float) CONVERSION_TICKS / 20);
    private static final int TICKS_PER_HEALTH_DRAIN = 20;
    private static final int HEALTH_DRAIN_AMOUNT = 1;
    private static final int HEALTH_DRAIN_RADIUS = 4;
    private static final int HEALTH_DRAIN_REQUIRED_FOR_CONVERSION = 20;

    private int conversionProgressTicks = 0;
    private int healthDrained = 0;
    private boolean didEntityDie = false;

    private int timeItemPlaced;
    public static final Vector3d itemRestingPositionTranslation = new Vector3d(0.5, 1 - (1 / 64.0), 0.5);
    public Vector3f itemRestingRotation = new Vector3f(0, 0, 0);
    private Vector3d itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(0, 0, 0);

    private int nextSpinStartTick = -1;
    private int nextSpinTotalRevolutions = 0;
    private float nextSpinHertz = 0;

    public ItemStack chainedIngredientToDisplay = ItemStack.EMPTY;
    private static final Map<PpIngredient, PpIngredient> chainedIngredients = new HashMap<>();

    public enum State {
        IDLE,
        CONVERTING,
        CONVERTED,
        FAILED
    }

    public State state = State.IDLE;

    public SanguineAltarBlockEntity(BlockPos pos, BlockState state) {
        super(Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected int getSlots() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SanguineAltarBlockEntity blockEntity) {
        spawnParticles(level, pos);
        updateSpinStatus(level, blockEntity);
        updateConversionProgress(level, pos, blockEntity);
    }

    private static void updateConversionProgress(Level level, BlockPos pos, SanguineAltarBlockEntity sanguineAltarBlockEntity) {
        if (sanguineAltarBlockEntity.state == State.IDLE) {
            if (sanguineAltarBlockEntity.getItem(0).isEmpty() || sanguineAltarBlockEntity.chainedIngredientToDisplay.isEmpty()) {
                return;
            }

            sanguineAltarBlockEntity.state = State.CONVERTING;
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), Sounds.SANGUINE_ALTAR_CONVERSION.get(), SoundSource.BLOCKS, 0.5F, 1, false);
        } else if (sanguineAltarBlockEntity.state == State.CONVERTING) {
            if (sanguineAltarBlockEntity.conversionProgressTicks >= CONVERSION_TICKS) {
                if (!level.isClientSide) {
                    // Server side, finish conversion and send packet
                    if (sanguineAltarBlockEntity.healthDrained >= HEALTH_DRAIN_REQUIRED_FOR_CONVERSION) {
                        changeState(level, pos, sanguineAltarBlockEntity, State.CONVERTED, true);
                    } else {
                        changeState(level, pos, sanguineAltarBlockEntity, State.FAILED, true);
                    }
                }
            } else {
                if (sanguineAltarBlockEntity.conversionProgressTicks % TICKS_PER_HEALTH_DRAIN == 0) {
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(HEALTH_DRAIN_RADIUS, HEALTH_DRAIN_RADIUS, HEALTH_DRAIN_RADIUS));

                    Vec3 blockPos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0.5, 0.5);
                    for (LivingEntity entity : entities) {
                        if (sanguineAltarBlockEntity.healthDrained < HEALTH_DRAIN_REQUIRED_FOR_CONVERSION) {
                            // Common side, drain health of entities
                            // TODO: Custom damage source
                            if (entity.hurt(DamageSource.GENERIC, HEALTH_DRAIN_AMOUNT)) {
                                sanguineAltarBlockEntity.healthDrained += HEALTH_DRAIN_AMOUNT;
                                if (entity.isDeadOrDying()) {
                                    sanguineAltarBlockEntity.didEntityDie = true;
                                }
                            }

                            if (level.isClientSide && sanguineAltarBlockEntity.getHealthDrainProgress() < 1.0f) {
                                if (!(entity instanceof LocalPlayer player) || (!player.isInvulnerable() && !player.isCreative() && !player.isSpectator())) {
                                    // Client side, spawn particles
                                    Vec3 towardsBlock = blockPos.subtract(entity.position()).normalize();
                                    // spawn particles
                                    for (int p = 0; p < level.random.nextInt(5); p++) {
                                        double velocity = level.random.nextDouble(0.1, 0.25);
                                        Vec3 vector = towardsBlock.multiply(velocity, velocity, velocity);
                                        Vec3 center = entity.getBoundingBox().getCenter();
                                        level.addParticle(Particles.BLOOD_EMITTER.get(), center.x, center.y, center.z, vector.x, vector.y, vector.z);
                                    }
                                }
                            }
                        } else if (!level.isClientSide) {
                            // If we have enough health drained, convert and send packet
                            // Disabled for now, animation is better without this
//                                changeState(level, pos, sanguineAltarBlockEntity, State.CONVERTED, true);
                            break;
                        }
                    }

                    if (!level.isClientSide) {
                        PotionsPlusPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new SanguineAltarConversionProgressPacket(pos, sanguineAltarBlockEntity.healthDrained));
                    }
                }
            }

            sanguineAltarBlockEntity.conversionProgressTicks++;
        }
    }

    private static void changeState(Level level, BlockPos pos, SanguineAltarBlockEntity sanguineAltarBlockEntity, State state, boolean clearItems) {
        if (clearItems) {
            sanguineAltarBlockEntity.clearContent();
        }

        sanguineAltarBlockEntity.state = state;
        PotionsPlusPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new SanguineAltarConversionStatePacket(pos, state));
    }

    private void resetConversionProgress() {
        conversionProgressTicks = 0;
        didEntityDie = false;
        healthDrained = 0;
        state = State.IDLE;
    }

    private static void updateSpinStatus(Level level, SanguineAltarBlockEntity blockEntity) {
        if (blockEntity.nextSpinStartTick < ClientTickHandler.total() - 100) {
            blockEntity.nextSpinStartTick = (int) ClientTickHandler.total() + level.getRandom().nextInt(0, 300);
            blockEntity.nextSpinTotalRevolutions = level.getRandom().nextInt(1, 3);
            blockEntity.nextSpinHertz = level.getRandom().nextFloat(0.25F, 1F);
        }
    }

    private static void spawnParticles(Level level, BlockPos pos) {
        if (level.isClientSide) {
            Vec3 posVec = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            for (int i = 0; i < 1; i++) {
                if (level.random.nextDouble() < 0.1) {
                    Vec3 particlePos = posVec.add(level.random.nextGaussian(0, 0.5), 0.25 + level.random.nextDouble(-0.125, 0.125), level.random.nextGaussian(0, 0.5));
                    level.addParticle(ParticleTypes.PORTAL, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public int getTimeItemPlaced() {
        return timeItemPlaced;
    }

    @Override
    public Vector3d getStartAnimationWorldPos() {
        return new Vector3d(itemAnimationStartingPosRelativeToBlockOrigin.x, itemAnimationStartingPosRelativeToBlockOrigin.y, itemAnimationStartingPosRelativeToBlockOrigin.z);
    }

    @Override
    public Vector3d getRestingPosition() {
        return itemRestingPositionTranslation;
    }

    @Override
    public Vector3f getRestingRotation() {
        return itemRestingRotation;
    }

    @Override
    public int getInputAnimationDuration() {
        return 20;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return chainedIngredients.containsKey(PpIngredient.of(stack));
    }

    public void onPlayerInsertItem(Player player) {
        Vec3 playerPosRelativeToBlockOrigin = player.getEyePosition();
        playerPosRelativeToBlockOrigin = playerPosRelativeToBlockOrigin.subtract(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        itemAnimationStartingPosRelativeToBlockOrigin = new Vector3d(playerPosRelativeToBlockOrigin.x, playerPosRelativeToBlockOrigin.y, playerPosRelativeToBlockOrigin.z);
        timeItemPlaced = ((int) ClientTickHandler.total());

        PpIngredient input = PpIngredient.of(getItem(0));
        if (chainedIngredients.containsKey(input)) {
            chainedIngredientToDisplay = chainedIngredients.get(input).getItemStack();
        } else {
            chainedIngredientToDisplay = ItemStack.EMPTY;
        }

        itemRestingRotation = new Vector3f(0, Utility.getHorizontalDirectionTowardsBlock(player.blockPosition(), this.getBlockPos()).toYRot(), 0);

        resetConversionProgress();
    }

    public void onPlayerRemoveItem(Player player) {
        timeItemPlaced = 0;
        chainedIngredientToDisplay = ItemStack.EMPTY;

        resetConversionProgress();
    }

    public int getNextSpinTickDelay() {
        return nextSpinStartTick - timeItemPlaced;
    }

    public int getNextSpinTotalRevolutions() {
        return nextSpinTotalRevolutions;
    }

    public float getNextSpinHertz() {
        return nextSpinHertz;
    }

    public void setHealthDrained(int healthDrained) {
        this.healthDrained = healthDrained;
    }

    public float getHealthDrainProgress() {
        return (float) healthDrained / (float) HEALTH_DRAIN_REQUIRED_FOR_CONVERSION;
    }

    public static void setRecipes(List<SanguineAltarRecipe> recipes) {
        chainedIngredients.clear();
        for (SanguineAltarRecipe recipe : recipes) {
            chainedIngredients.put(PpIngredient.of(recipe.getIngredients().get(0)), PpIngredient.of(recipe.getResultItem()));
        }
    }
}

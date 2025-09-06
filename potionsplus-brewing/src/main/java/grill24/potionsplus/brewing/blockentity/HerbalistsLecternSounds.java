package grill24.potionsplus.blockentity;

import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

public class HerbalistsLecternSounds {
    private SoundInstance appearSound;
    private SoundInstance disappearSound;

    public void playSoundAppear(BlockPos pos) {
        this.appearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_APPEAR.value(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                pos.getX(), pos.getY(), pos.getZ(), true);

        Utility.playSoundStopOther(this.appearSound, this.disappearSound);
    }

    public void playSoundDisappear(BlockPos pos) {
        this.disappearSound = Utility.createSoundInstance(
                Sounds.HERBALISTS_LECTERN_DISAPPEAR.value(),
                SoundSource.BLOCKS,
                0.25F, 1.0F,
                false, 0,
                SoundInstance.Attenuation.LINEAR,
                pos.getX(), pos.getY(), pos.getZ(), true);

        Utility.playSoundStopOther(this.disappearSound, this.appearSound);
    }

    public HerbalistsLecternSounds() {
    }
}

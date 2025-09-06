package grill24.potionsplus.skill.ability.instance;

public class DoubleJump extends AbilityInstanceType<DoubleJumpAbilityInstanceData> {
    public DoubleJump() {
        super(DoubleJumpAbilityInstanceData.CODEC, DoubleJumpAbilityInstanceData.STREAM_CODEC);
    }
}

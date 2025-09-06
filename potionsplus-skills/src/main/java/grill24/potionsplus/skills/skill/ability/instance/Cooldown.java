package grill24.potionsplus.skill.ability.instance;

public class Cooldown extends AbilityInstanceType<CooldownAbilityInstanceData> {
    public Cooldown() {
        super(CooldownAbilityInstanceData.CODEC, CooldownAbilityInstanceData.STREAM_CODEC);
    }
}

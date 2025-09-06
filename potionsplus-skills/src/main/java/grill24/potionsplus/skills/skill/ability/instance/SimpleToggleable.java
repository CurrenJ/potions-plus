package grill24.potionsplus.skill.ability.instance;

public class SimpleToggleable extends AbilityInstanceType<SimpleAbilityInstanceData> {
    public SimpleToggleable() {
        super(SimpleAbilityInstanceData.CODEC, SimpleAbilityInstanceData.STREAM_CODEC);
    }
}

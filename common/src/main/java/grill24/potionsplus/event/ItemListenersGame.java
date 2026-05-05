package grill24.potionsplus.event;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemListenersGame {
    public static final int durationUpgradeTextAnimationDurationTicks = 10;

    public static List<Component> animateComponentTextStartTime(List<List<Component>> components, float animationStartTimestamp) {
        List<Component> animatedComponents = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            List<Component> component = components.get(i);
            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = animateComponentText(component, durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            animatedComponents.add(animatedComponent.getFirst());
        }
        return animatedComponents;
    }

    public static Pair<MutableComponent, Integer> animateComponentText(List<Component> component, float duration, int delayTicks, float animationStartTimestamp) {
        float f = RUtil.lerp(0.0F, 1.0F, RUtil.easeOutSine(Math.clamp((ClientTickHandler.total() - animationStartTimestamp - delayTicks) / duration, 0.0F, 1.0F)));
        f = Math.clamp(f, 0.0F, 1.0F);
        return animateComponentText(component, f);
    }

    public static Pair<MutableComponent, Integer> animateComponentText(List<Component> component, float animationProgress) {
        String totalString = component.stream().map(Component::getString).collect(Collectors.joining());
        int splitIndex = Math.round(animationProgress * totalString.length());

        int index = 0;
        List<MutableComponent> components = new ArrayList<>();
        for (Component c : component) {
            String text = c.getString();
            if (index < splitIndex) {
                int splitIndexInComponent = Math.clamp(splitIndex - index, 0, text.length());
                String truncatedText = text.substring(0, splitIndexInComponent);
                if (!truncatedText.isEmpty()) {
                    MutableComponent mutableComponent = Component.literal(truncatedText).withStyle(c.getStyle());
                    components.add(mutableComponent);
                }
            }
            index += text.length();
        }

        if (components.isEmpty()) {
            return Pair.of(Component.empty(), 0);
        }

        MutableComponent finalComponent = Component.empty();
        for (MutableComponent mutableComponent : components) {
            finalComponent = finalComponent.append(mutableComponent);
        }
        return Pair.of(finalComponent, splitIndex);
    }
}

package dev.minixr9k.types;

import dev.minixr9k.types.components.ConsumableComponent;
import dev.minixr9k.types.components.FoodComponent;

import java.util.List;

public class ItemComponent {

    private final String componentType;
    private final Object componentValue;

    public ItemComponent() {
        this.componentType = "";
        this.componentValue = null;
    }

    public ItemComponent(String componentType) {
        this.componentType = componentType;
        this.componentValue = null;
    }

    public ItemComponent(String componentType, Object componentValue) {
        this.componentType = componentType;
        this.componentValue = componentValue;
    }

    public String getComponentType() {
        return componentType;
    }

    public Object getComponentValue() {
        return componentValue;
    }

    public int getValueAsInt() {
        if (componentValue instanceof Number) {
            return ((Number) componentValue).intValue();
        }
        return 0;
    }

    public float getValueAsFloat() {
        if (componentValue instanceof Number) {
            return ((Number) componentValue).floatValue();
        }
        return 0;
    }

    public String getValueAsString() {
        return componentValue != null ? componentValue.toString() : "";
    }

    @SuppressWarnings("unchecked")
    public List<String> getValueAsListString() {
        if (componentValue instanceof List<?>) {
            List<?> rawList = (List<?>) componentValue;
            return rawList.stream()
                    .map(obj -> obj != null ? obj.toString() : "")
                    .toList();
        }
        if (componentValue instanceof String) {
            return List.of((String) componentValue);
        }
        return List.of();
    }

    public FoodComponent getValueAsFood() {
        if (componentValue instanceof FoodComponent foodComponent)
            return foodComponent;
        return null;
    }

    public ConsumableComponent getValueAsConsumable() {
        if (componentValue instanceof ConsumableComponent consumableComponent)
            return consumableComponent;
        return null;
    }
}

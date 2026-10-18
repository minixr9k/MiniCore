package dev.minixr9k.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStack {
    private final String type;
    private final short count;
    private List<ItemComponent> components = new ArrayList<>();

    public ItemStack(String type, short count) {
        this.type = type;
        this.count = count;
    }

    public ItemStack(String type, short count, List<ItemComponent> components) {
        this.type = type;
        this.count = count;
        this.components = components;
    }

    public String getType() {
        return type;
    }

    public short getCount() {
        return count;
    }

    public List<ItemComponent> getComponents() {
        return components;
    }

    public void setComponents(ItemComponent... components) {
        Collections.addAll(this.components, components);
    }

    public void addComponent(ItemComponent component) {
        components.add(component);
    }

    public void removeComponent(ItemComponent component) {
        components.remove(component);
    }

    public Object getComponentValue(String componentType) {
        for (ItemComponent component : components) {
            if (component.getComponentType().equals(componentType)) {
                return component.getComponentValue();
            }
        }
        return null;
    }

    public boolean hasSameComponents(ItemStack other) {
        if (this.components.size() != other.getComponents().size()) {
            return false;
        }

        for (ItemComponent comp1 : this.components) {
            boolean found = false;
            for (ItemComponent comp2 : other.getComponents()) {
                if (comp1.getComponentType().equals(comp2.getComponentType())) {
                    Object val1 = comp1.getComponentValue();
                    Object val2 = comp2.getComponentValue();

                    if (val1 == null ? val2 == null : val1.equals(val2)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return false;
        }
        return true;
    }
}

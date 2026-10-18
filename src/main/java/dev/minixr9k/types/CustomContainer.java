package dev.minixr9k.types;

import dev.minixr9k.api.event.InventoryClickEvent;

import java.util.function.Consumer;

public class CustomContainer {

    private final int windowId;
    private final WindowType type;
    private final String title;
    private final int size;
    private final ItemStack[] slots;

    private Consumer<InventoryClickEvent> clickHandler;

    public CustomContainer(int windowId, WindowType type, String title, int size) {
        this.windowId = windowId;
        this.type = type;
        this.title = title;
        this.size = size;

        this.slots = new ItemStack[size + 36];
    }

    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < slots.length) {
            slots[slot] = item;
        }
    }

    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < slots.length) {
            return slots[slot];
        }
        return null;
    }

    public void setClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void handleClick(InventoryClickEvent event) {
        if (clickHandler != null) {
            clickHandler.accept(event);
        }
    }

    public int getWindowId() { return windowId; }
    public WindowType getType() { return type; }
    public String getTitle() { return title; }
    public int getSize() { return size; }
    public ItemStack[] getSlots() { return slots; }
}
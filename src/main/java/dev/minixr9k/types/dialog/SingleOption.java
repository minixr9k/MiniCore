package dev.minixr9k.types.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleOption {

    private final String type = "minecraft:single_option";
    private String key;
    private String text;
    private boolean labelVisible = true;
    private int width = 200;
    private List<Option> options = new ArrayList<>();

    public SingleOption(String text, String key, Option... options) {
        this.text = text;
        this.key = key;
        if (options != null) {
            Collections.addAll(this.options, options);
        }
    }

    public SingleOption width(int width) {
        this.width = width;
        return this;
    }

    public SingleOption labelVisible(boolean visible) {
        this.labelVisible = visible;
        return this;
    }

    public SingleOption option(Option option) {
        this.options.add(option);
        return this;
    }

    public SingleOption options(Option... options) {
        if (options != null) {
            Collections.addAll(this.options, options);
        }
        return this;
    }

    public static class Option {
        private String id;
        private String display;
        private boolean initial = false;

        public Option(String id, String display) {
            this(id, display, false);
        }

        public Option(String id, String display, boolean initial) {
            this.id = id;
            this.display = display;
            this.initial = initial;
        }

        public Option initial(boolean initial) {
            this.initial = initial;
            return this;
        }

        public String getId() { return id; }
        public String getDisplay() { return display; }
        public boolean isInitial() { return initial; }
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public boolean isLabelVisible() {
        return labelVisible;
    }

    public int getWidth() {
        return width;
    }

    public List<Option> getOptions() {
        return options;
    }
}
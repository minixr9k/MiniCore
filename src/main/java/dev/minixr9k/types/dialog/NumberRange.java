package dev.minixr9k.types.dialog;

public class NumberRange {

    private final String type = "minecraft:number_range";
    private String key;
    private String text;
    private boolean labelVisible = true;
    private String labelFormat = "options.generic_value";
    private int width = 200;

    private float start;
    private float end;
    private float step = -1f; // Если не задано, то шаг свободный
    private float initial;
    private boolean hasInitial = false;

    public NumberRange(String text, String key, float start, float end) {
        this.text = text;
        this.key = key;
        this.start = start;
        this.end = end;
        this.initial = start + (end - start) / 2f;
    }

    public NumberRange width(int width) {
        this.width = width;
        return this;
    }

    public NumberRange labelVisible(boolean visible) {
        this.labelVisible = visible;
        return this;
    }

    public NumberRange labelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
        return this;
    }

    public NumberRange step(float step) {
        this.step = step;
        return this;
    }

    public NumberRange initial(float initial) {
        this.initial = initial;
        this.hasInitial = true;
        return this;
    }

    public String getType() { return type; }
    public String getKey() { return key; }
    public String getText() { return text; }
    public boolean isLabelVisible() { return labelVisible; }
    public String getLabelFormat() { return labelFormat; }
    public int getWidth() { return width; }
    public float getStart() { return start; }
    public float getEnd() { return end; }
    public float getStep() { return step; }
    public float getInitial() { return initial; }
    public boolean hasInitial() { return hasInitial; }
}
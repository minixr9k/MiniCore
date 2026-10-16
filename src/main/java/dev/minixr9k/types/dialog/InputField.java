package dev.minixr9k.types.dialog;

public class InputField {

    private String type = "minecraft:text";
    private String text = ""; // label
    private String key = "";
    private int width = 200;
    private boolean labelVisible = true;
    private String initial = "";
    private int maxLength = 32;

    private boolean isMultiLine = false;
    private int maxLines = -1;
    private int height = -1;

    public InputField(String text, String key) {
        this.text = text;
        this.key = key;
    }

    public InputField width(int width) {
        this.width = width;
        return this;
    }

    public InputField labelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
        return this;
    }

    public InputField initial(String initial) {
        this.initial = initial;
        return this;
    }

    public InputField maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public InputField multiLine(boolean multiLine) {
        isMultiLine = multiLine;
        return this;
    }

    public InputField maxLines(int maxLines) {
        this.maxLines = maxLines;
        return this;
    }

    public InputField height(int height) {
        this.height = height;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public int getWidth() {
        return width;
    }

    public boolean isLabelVisible() {
        return labelVisible;
    }

    public String getInitial() {
        return initial;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isMultiLine() {
        return isMultiLine;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public int getHeight() {
        return height;
    }
}

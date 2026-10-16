package dev.minixr9k.types.dialog;

public class CheckBox {

    private String type = "minecraft:boolean";
    private String text = ""; // label
    private String key = "";
    private boolean checked = false;
    private String whenFalse = "";
    private String whenTrue = "";

    public CheckBox(String text, String key) {
        this.text = text;
        this.key = key;
    }

    public CheckBox checked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public CheckBox whenFalse(String text) {
        this.whenFalse = text;
        return this;
    }

    public CheckBox whenTrue(String text) {
        this.whenTrue = text;
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

    public boolean isChecked() {
        return checked;
    }

    public String getWhenFalse() {
        return whenFalse;
    }

    public String getWhenTrue() {
        return whenTrue;
    }
}

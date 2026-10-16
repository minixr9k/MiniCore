package dev.minixr9k.types.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dialog {

    private final DialogType type;
    private String title;
    private final List<Object> body;
    private final List<Object> inputs;
    private final List<Object> actions;
    private int columns = 2;
    private boolean closeable = true;
    private CloseAction closeAction = CloseAction.CLOSE;

    public enum CloseAction {
        CLOSE,
        NONE,
        WAIT_FOR_RESPONSE
    }

    public Dialog(DialogType type) {
        this.type = type;
        this.title = "";
        body = new ArrayList<>();
        actions = new ArrayList<>();
        inputs = new ArrayList<>();
    }

    public Dialog title(String title) {
        this.title = title;
        return this;
    }

    public Dialog body(Object... objects) {
        Collections.addAll(body, objects);
        return this;
    }

    public Dialog inputs(Object... objects) {
        Collections.addAll(inputs, objects);
        return this;
    }

    public Dialog actions(Object... objects) {
        Collections.addAll(actions, objects);
        return this;
    }

    public Dialog columns(int columns) {
        this.columns = columns;
        return this;
    }

    public Dialog closeable(boolean closeable) {
        this.closeable = closeable;
        return this;
    }

    public Dialog closeAction(CloseAction closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    public DialogType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public List<Object> getBody() {
        return body;
    }

    public List<Object> getInputs() {
        return inputs;
    }

    public List<Object> getActions() {
        return actions;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public CloseAction getCloseAction() {
        return closeAction;
    }
}

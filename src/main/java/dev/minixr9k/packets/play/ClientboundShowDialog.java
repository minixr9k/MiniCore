package dev.minixr9k.packets.play;

import dev.minixr9k.types.dialog.*;
import dev.minixr9k.utils.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import static dev.minixr9k.utils.ProtocolUtils.*;

public class ClientboundShowDialog implements MinecraftPacket {

    private final Dialog dialog;

    public ClientboundShowDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void write(ByteBuf out, int protocolVersion) {

        writeVarInt(out, 0);

        out.writeByte(10); // tag compound

        out.writeByte(8); // tag string
        writeNBTString(out, "type");
        writeNBTString(out, "minecraft:" + dialog.getType().name().toLowerCase());

        out.writeByte(8); // tag string
        writeNBTString(out, "title");
        writeNBTString(out, dialog.getTitle());

        if (!dialog.getBody().isEmpty()) {
            out.writeByte(10);
            writeNBTString(out, "body");

            for (Object obj : dialog.getBody()) {
                if (obj instanceof PlainMessage element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "type");
                    writeNBTString(out, element.getType());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "contents");
                    writeNBTString(out, element.getContents());

                    if (element.getWidth() != 200) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "width");
                        out.writeInt(element.getWidth());
                    }
                }
            }

            out.writeByte(0); // tag end
        }

        if (dialog.getType() == DialogType.MULTI_ACTION) {
            out.writeByte(9);
            writeNBTString(out, "inputs");
            out.writeByte(10);

            out.writeInt(dialog.getInputs().size());
            for (Object obj : dialog.getInputs()) {
                if (obj instanceof InputField element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "type");
                    writeNBTString(out, element.getType());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "key");
                    writeNBTString(out, element.getKey());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "label");
                    writeNBTString(out, element.getText());

                    if (element.getWidth() != 200) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "width");
                        out.writeInt(element.getWidth());
                    }

                    if (!element.isLabelVisible()) {
                        out.writeByte(1); // tag byte
                        writeNBTString(out, "label_visible");
                        out.writeBoolean(element.isLabelVisible());
                    }

                    if (!element.getInitial().isEmpty()) {
                        out.writeByte(8); // tag string
                        writeNBTString(out, "label");
                        writeNBTString(out, element.getInitial());
                    }

                    if (element.getMaxLength() != 32) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "max_length");
                        out.writeInt(element.getWidth());
                    }

                    if (element.isMultiLine()) {
                        out.writeByte(10);
                        writeNBTString(out, "multiline");

                        if (element.getMaxLines() != -1) {
                            out.writeByte(3); // tag int
                            writeNBTString(out, "max_lines");
                            out.writeInt(element.getMaxLines());
                        }

                        if (element.getHeight() != -1) {
                            out.writeByte(3); // tag int
                            writeNBTString(out, "height");
                            out.writeInt(element.getHeight());
                        }

                        out.writeByte(0);
                    }

                    out.writeByte(0);
                }
                else if (obj instanceof CheckBox element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "type");
                    writeNBTString(out, element.getType());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "key");
                    writeNBTString(out, element.getKey());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "label");
                    writeNBTString(out, element.getText());

                    if (element.isChecked()) {
                        out.writeByte(1); // tag byte
                        writeNBTString(out, "initial");
                        out.writeBoolean(element.isChecked());
                    }

                    if (!element.getWhenFalse().isEmpty()) {
                        out.writeByte(8); // tag string
                        writeNBTString(out, "on_false");
                        writeNBTString(out, element.getWhenFalse());
                    }

                    if (!element.getWhenTrue().isEmpty()) {
                        out.writeByte(8); // tag string
                        writeNBTString(out, "on_true");
                        writeNBTString(out, element.getWhenFalse());
                    }

                    out.writeByte(0);
                }
                else if (obj instanceof SingleOption element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "type");
                    writeNBTString(out, element.getType());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "key");
                    writeNBTString(out, element.getKey());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "label");
                    writeNBTString(out, element.getText());

                    if (!element.isLabelVisible()) {
                        out.writeByte(1); // tag byte
                        writeNBTString(out, "label_visible");
                        out.writeBoolean(element.isLabelVisible());
                    }

                    if (element.getWidth() != 200) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "width");
                        out.writeInt(element.getWidth());
                    }

                    if (!element.getOptions().isEmpty()) {
                        out.writeByte(9);
                        writeNBTString(out, "options");
                        out.writeByte(10);
                        out.writeInt(element.getOptions().size());

                        for (SingleOption.Option opt : element.getOptions()) {
                            out.writeByte(8);
                            writeNBTString(out, "id");
                            writeNBTString(out, opt.getId());

                            out.writeByte(8);
                            writeNBTString(out, "display");
                            writeNBTString(out, opt.getDisplay());

                            if (opt.isInitial()) {
                                out.writeByte(1);
                                writeNBTString(out, "initial");
                                out.writeBoolean(true);
                            }

                            out.writeByte(0);
                        }

                        out.writeByte(0);
                    }
                }
                else if (obj instanceof NumberRange element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "type");
                    writeNBTString(out, element.getType());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "key");
                    writeNBTString(out, element.getKey());

                    out.writeByte(8); // tag string
                    writeNBTString(out, "label");
                    writeNBTString(out, element.getText());

                    if (!element.isLabelVisible()) {
                        out.writeByte(1); // tag byte
                        writeNBTString(out, "label_visible");
                        out.writeBoolean(false);
                    }

                    if (!element.getLabelFormat().equals("options.generic_value")) {
                        out.writeByte(8); // tag string
                        writeNBTString(out, "label_format");
                        writeNBTString(out, element.getLabelFormat());
                    }

                    if (element.getWidth() != 200) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "width");
                        out.writeInt(element.getWidth());
                    }

                    out.writeByte(5); // tag float
                    writeNBTString(out, "start");
                    out.writeFloat(element.getStart());

                    out.writeByte(5); // tag float
                    writeNBTString(out, "end");
                    out.writeFloat(element.getEnd());

                    if (element.getStep() != -1f) {
                        out.writeByte(5); // tag float
                        writeNBTString(out, "step");
                        out.writeFloat(element.getStep());
                    }

                    if (element.hasInitial()) {
                        out.writeByte(5); // tag float
                        writeNBTString(out, "initial");
                        out.writeFloat(element.getInitial());
                    }

                    out.writeByte(0);
                }
            }
        }

        if (dialog.getType() == DialogType.MULTI_ACTION) {
            out.writeByte(9);
            writeNBTString(out, "actions");
            out.writeByte(10);

            out.writeInt(dialog.getActions().size());

            for (Object obj : dialog.getActions()) {
                if (obj instanceof ActionButton element) {
                    out.writeByte(8); // tag string
                    writeNBTString(out, "label");
                    writeNBTString(out, element.getText());

                    if (element.getWidth() != 150) {
                        out.writeByte(3); // tag int
                        writeNBTString(out, "width");
                        out.writeInt(element.getWidth());
                    }

                    if (!element.getCustomAction().isEmpty()) {
                        out.writeByte(10);
                        writeNBTString(out, "action");

                        out.writeByte(8); // tag string
                        writeNBTString(out, "type");
                        writeNBTString(out, "custom");

                        out.writeByte(8); // tag string
                        writeNBTString(out, "id");
                        writeNBTString(out, element.getCustomAction());

                        out.writeByte(0);
                    }

                    out.writeByte(0);
                }
            }
        }

        if (dialog.getType() == DialogType.MULTI_ACTION
        || dialog.getType() == DialogType.SERVER_LINKS
        || dialog.getType() == DialogType.DIALOG_LIST) {
            if (dialog.getColumns() != 2) {
                out.writeByte(3); // tag int
                writeNBTString(out, "columns");
                out.writeInt(dialog.getColumns());
            }
        }

        if (!dialog.isCloseable()) {
            out.writeByte(1); // tag byte
            writeNBTString(out, "can_close_with_escape");
            out.writeBoolean(dialog.isCloseable());
        }

        if (dialog.getCloseAction() != Dialog.CloseAction.CLOSE) {
            out.writeByte(8); // tag string
            writeNBTString(out, "after_action");
            switch (dialog.getCloseAction()) {
                case NONE -> writeNBTString(out, "none");
                case CLOSE -> writeNBTString(out, "close");
                case WAIT_FOR_RESPONSE -> writeNBTString(out, "wait_for_response");
            }
        }


        out.writeByte(0); // tag end
    }

    @Override
    public void read(ByteBuf in, int protocolVersion) {

    }

    @Override
    public int getPacketId(int protocolVersion) {
        return 0x85;
    }
}

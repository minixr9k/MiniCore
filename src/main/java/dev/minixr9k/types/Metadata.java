package dev.minixr9k.types;

public enum Metadata {
    BYTE(0),
    VAR_INT(1),
    VAR_LONG(2),
    FLOAT(3),
    STRING(4),
    TEXT_COMPONENT(5),
    OPTIONAL_TEXT_COMPONENT(6),
    SLOT(7),
    BOOLEAN(8),
    ROTATIONS(9),
    POSITION(10),
    OPTIONAL_POSITION(11),
    DIRECTION(12),
    BLOCK_STATE(14),
    POSE(21),
    CAT_VARIANT(22),
    COW_VARIANT(23),
    WOLF_VARIANT(24),
    WOLF_SOUND_VARIANT(25),
    FROG_VARIANT(26),
    PIG_VARIANT(27),
    CHICKEN_VARIANT(28),
    VECTOR3F(33),
    QUATERNION(34);

    private final int id;
    Metadata(int id) { this.id = id; }
    public int getId() { return id; }
}
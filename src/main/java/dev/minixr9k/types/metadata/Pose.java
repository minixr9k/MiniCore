package dev.minixr9k.types.metadata;

public enum Pose {
    STANDING(0),
    FALL_FLYING(1),
    SLEEPING(2),
    SWIMMING(3),
    SPIN_ATTACK(4),
    SNEAKING(5),
    LONG_JUMPING(6),
    DYING(7),
    CROAKING(8),
    USING_TONGUE(9),
    SITTING(10),
    ROARING(11),
    SNIFFING(12),
    EMERGING(13),
    DIGGING(14),
    SLIDING(15),
    SHOOTING(16),
    INHALING(17)
    ;

    private final int id;

    Pose(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

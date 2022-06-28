package me.fizzika.tankirating.enums;

public enum TrackTargetStatus {

    ACTIVE, // Account is regularly updating
    FROZEN, // Last account updates are fails, but system made new attempts
    DISABLED; // Account is disabled, system is not updating it

    public boolean isUpdatable() {
        return this != DISABLED;
    }

}

package me.fizzika.tankirating.enums.track;

public enum TrackTargetStatus {

    ACTIVE, // Account is regularly updating
    SLEEP, // In the last period (active-to-sleep-timeout) player is not played. System will not update this player frequently
    FROZEN, // Last account updates are fails, but system made new attempts
    DISABLED; // Account is disabled, system is not actively updating it
}
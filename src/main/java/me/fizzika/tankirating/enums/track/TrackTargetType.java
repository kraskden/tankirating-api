package me.fizzika.tankirating.enums.track;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TrackTargetType {

    ACCOUNT(null), GROUP("~");

    private final String namePrefix;

    public static TrackTargetType fromName(String name) {
        for (TrackTargetType t : values()) {
            if (t.namePrefix != null && name.startsWith(t.namePrefix)) {
                return t;
            }
        }
        return ACCOUNT;
    }

}

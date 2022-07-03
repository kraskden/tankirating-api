package me.fizzika.tankirating.enums.track;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum GroupMeta {

    LEGENDS(1_600_000, null);

    private final Integer minScore;
    private final Integer maxScore;

    private static final Map<String, GroupMeta> VALUES_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Optional<GroupMeta> fromName(String name) {
        return Optional.ofNullable(VALUES_MAP.get(name.toUpperCase()));
    }

}

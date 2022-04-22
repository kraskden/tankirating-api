package me.fizzika.tankirating.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum TankiSupply {

    AID(10007274L),
    DD(10007271L),
    DA(10007268L),
    N2O(10007280L),
    MINE(10007277L),
    GOLDBOX(920004863750L),
    BATTERY(920009489701L),
    NUCLEAR(921009756684L);

    private static final Map<Long, TankiSupply> ID_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(TankiSupply::getId, Function.identity()));

    private final Long id;

    public static Optional<TankiSupply> getById(Long id) {
        return Optional.ofNullable(ID_MAP.get(id));
    }

}

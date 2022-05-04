package me.fizzika.tankirating.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Function;

/**
 * Pair implementation for two same types
 * @param <T> type for each param in the pair
 */
@Getter
@ToString
@EqualsAndHashCode
public class Pair<T> {

    private final T first;
    private final T second;

    private Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public static <T> Pair<T> of(T fst, T snd) {
        return new Pair<>(fst, snd);
    }

    public <U> Pair<U> map(Function<T, U> mapFn) {
        return new Pair<>(mapFn.apply(first), mapFn.apply(second));
    }

}

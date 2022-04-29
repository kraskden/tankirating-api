package me.fizzika.tankirating.util;

import me.fizzika.tankirating.model.TrackData;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TrackUtils {

    public static <K, T> void mergeMap(Map<K, T> fst, Map<K, T> snd,
                                       BiConsumer<T, T> transformer,
                                       Function<T, T> copyCreator) {
        for (K key : snd.keySet()) {
            T val = snd.get(key);
            if (fst.containsKey(key)) {
                transformer.accept(fst.get(key), val);
            } else {
                fst.put(key, copyCreator.apply(val));
            }
        }
    }

}

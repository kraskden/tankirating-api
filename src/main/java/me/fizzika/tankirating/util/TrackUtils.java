package me.fizzika.tankirating.util;

import me.fizzika.tankirating.model.TrackModel;

import java.util.Map;
import java.util.function.Function;

public class TrackUtils {

    public static <K, T extends TrackModel<T>> void addMap(Map<K, T> map, Map<K, T> adder,
                                                    Function<T, T> copyCreator) {
        for (K key : adder.keySet()) {
            T val = adder.get(key);
            if (map.containsKey(key)) {
                map.get(key).add(val);
            } else {
                map.put(key, copyCreator.apply(val));
            }
        }
    }

}

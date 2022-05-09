package me.fizzika.tankirating.util;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TrackUtils {

    /**
     * Merge {@code addMap} values into {@code baseMap} values. If {@code addMap} key is exists in the {@code baseMap}, that apply
     * {@code valuesCombiner} function to the {@code baseMap[key]} and {@code addMap[key]};
     * If {@code addMap} key in not exists in the {@code baseMap}, then put into the {@code baseMap} copy of the {@code addMap[key]}
     *
     * For examples, see
     * {@link me.fizzika.tankirating.model.track_data.TrackFullData#add} and
     * {@link me.fizzika.tankirating.model.track_data.TrackFullData#sub}
     */
    public static <K, T> void mergeMapValues(Map<K, T> baseMap, Map<K, T> addMap,
                                             BiConsumer<T, T> valuesCombiner,
                                             Function<T, T> copyCreator) {
        for (K key : addMap.keySet()) {
            T val = addMap.get(key);
            if (baseMap.containsKey(key)) {
                valuesCombiner.accept(baseMap.get(key), val);
            } else {
                baseMap.put(key, copyCreator.apply(val));
            }
        }
    }

}

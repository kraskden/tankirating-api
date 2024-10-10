package me.fizzika.tankirating.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.fizzika.tankirating.exceptions.tracking.InvalidDiffException;
import me.fizzika.tankirating.exceptions.tracking.InvalidTrackDataException;
import me.fizzika.tankirating.exceptions.tracking.SkipDiffException;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.model.track_data.TrackPlayData;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;

public class TrackUtils {


    public static void validateDiffData(TrackFullData data,
                                        long diffPeriodSeconds,
                                        Duration timeInaccuracyInterval,
                                        boolean strict) throws InvalidTrackDataException {
        long seconds = data.getBase().getTime();
        long inaccuracySeconds = timeInaccuracyInterval.toSeconds();

        Function<String, InvalidTrackDataException> exCreator = strict ? InvalidDiffException::new : SkipDiffException::new;

        if (seconds < -inaccuracySeconds) {
            throw exCreator.apply(String.format("Diff seconds cannot be negative: %d", seconds));
        }

        if (seconds > diffPeriodSeconds) {
            throw exCreator.apply(String.format("Diff seconds cannot be greater that" +
                    "period duration: %d > %d", seconds, diffPeriodSeconds));
        }
    }

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
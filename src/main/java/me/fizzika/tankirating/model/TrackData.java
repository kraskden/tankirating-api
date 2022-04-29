package me.fizzika.tankirating.model;

public interface TrackData<T extends TrackData<T>> {
    
    void add(T other);

    void sub(T other);

    T copy();

    static <T extends TrackData<T>> T diff(T fst, T snd) {
        T res = fst.copy();
        res.sub(snd);
        return res;
    }

}

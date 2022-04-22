package me.fizzika.tankirating.model;

public interface TrackModel<T extends TrackModel<T>> {
    
    void add(T other);

}

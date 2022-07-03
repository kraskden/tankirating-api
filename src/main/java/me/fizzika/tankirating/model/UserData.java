package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserData<T extends TrackData<T>> {

    private boolean hasPremium;

    private T trackData;

}

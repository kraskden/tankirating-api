package me.fizzika.tankirating.model;

import lombok.Data;

@Data
public class OnlineData {

    private int inbattles;
    private int online;

    public OnlineData mutableAdd(OnlineData other) {
        this.inbattles += other.inbattles;
        this.online += other.online;
        return this;
    }

}

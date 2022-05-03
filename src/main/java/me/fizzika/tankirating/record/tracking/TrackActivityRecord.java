package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.track.TrackActivityType;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;

@Entity
@Table(name = "track_activity")
@Getter
@Setter
@ToString
public class TrackActivityRecord extends IdRecord {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    private TrackRecord track;

    private String name;

    @Enumerated(EnumType.STRING)
    private TrackActivityType type;

    private int score;

    private long time;

}

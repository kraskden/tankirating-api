package me.fizzika.tankirating.record.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;

@Entity
@Table(name = "track_activity")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackActivityRecord extends IdRecord {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private TrackRecord track;

    private String name;

    @Enumerated(EnumType.STRING)
    private TrackActivityType type;

    private Integer score;

    private Long time;

}

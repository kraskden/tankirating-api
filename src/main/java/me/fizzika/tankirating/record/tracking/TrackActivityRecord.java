package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.IdRecord;

import jakarta.persistence.*;

@Entity
@Table(name = "track_activity")
@Getter
@Setter
@ToString
public class TrackActivityRecord extends IdRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_activity_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    private TrackRecord track;

    @Column(name = "entity_id")
    private Short entityId;

    private long score;

    private long time;

}
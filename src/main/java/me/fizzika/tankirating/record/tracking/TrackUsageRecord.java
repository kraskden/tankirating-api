package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.IdRecord;

import jakarta.persistence.*;

@Entity
@Table(name = "track_usage")
@Getter
@Setter
@ToString
public class TrackUsageRecord extends IdRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_usage_seq")
    private Long id;

    @Column(name = "entity_id")
    private Short entityId;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private TrackRecord track;

    private int usages;

}
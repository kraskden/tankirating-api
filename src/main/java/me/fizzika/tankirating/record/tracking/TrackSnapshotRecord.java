package me.fizzika.tankirating.record.tracking;

import lombok.*;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "snapshot")
public class TrackSnapshotRecord extends IdRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snapshot_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackRecord;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    @ToString.Exclude
    private TrackTargetRecord target;

    public TrackSnapshotRecord(TrackRecord trackRecord, LocalDateTime timestamp, TrackTargetRecord target) {
        this.trackRecord = trackRecord;
        this.timestamp = timestamp;
        this.target = target;
    }

}

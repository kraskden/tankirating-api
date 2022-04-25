package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Table(name = "snapshot")
public class TrackSnapshotRecord extends IdRecord {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackData;

    private LocalDateTime timestamp;

    private UUID targetId;

}

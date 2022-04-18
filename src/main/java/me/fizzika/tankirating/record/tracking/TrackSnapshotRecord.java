package me.fizzika.tankirating.record.tracking;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "snapshot")
@Data
public class TrackSnapshotRecord {

    @Id
    @Column(name = "track_id")
    private UUID trackId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackData;

    @Column(name = "has_premium")
    private Boolean hasPremium;

}

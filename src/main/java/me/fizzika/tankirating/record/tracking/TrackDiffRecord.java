package me.fizzika.tankirating.record.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.enums.TrackDiffPeriod;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "diff_track")
@Data
public class TrackDiffRecord {

    @Id
    @Column(name = "track_id")
    private UUID trackId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackData;

    @Enumerated(EnumType.STRING)
    private TrackDiffPeriod period;

    @Column(name = "premium_days")
    private int premiumDays;

}

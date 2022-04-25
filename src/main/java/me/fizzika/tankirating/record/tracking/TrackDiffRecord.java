package me.fizzika.tankirating.record.tracking;

import lombok.*;
import me.fizzika.tankirating.enums.TrackDiffPeriod;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diff")
@Getter
@Setter
@ToString
public class TrackDiffRecord extends IdRecord {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackData;

    @Enumerated(EnumType.STRING)
    private TrackDiffPeriod period;

    private LocalDateTime from;

    private LocalDateTime to;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    @ToString.Exclude
    private TrackTargetRecord target;

}

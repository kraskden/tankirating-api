package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.TrackDiffPeriod;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diff")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TrackDiffRecord extends IdRecord {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackRecord;

    @Enumerated(EnumType.STRING)
    private TrackDiffPeriod period;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private LocalDateTime trackStart;

    private LocalDateTime trackEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    @ToString.Exclude
    private TrackTargetRecord target;

}

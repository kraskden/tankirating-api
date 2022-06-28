package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.record.IdRecord;
import me.fizzika.tankirating.record.PeriodRecord;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diff")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TrackDiffRecord extends PeriodRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diff_seq")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    private TrackRecord trackRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    @ToString.Exclude
    private TrackTargetRecord target;

    private Integer premiumDays;

    private Integer maxScore;

}

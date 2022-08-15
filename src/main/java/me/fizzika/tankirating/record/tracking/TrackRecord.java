package me.fizzika.tankirating.record.tracking;

import lombok.*;
import me.fizzika.tankirating.record.IdRecord;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "track")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TrackRecord extends IdRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_seq")
    private Long id;

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    @Column(insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Float kd;

    @Column(insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Float kt;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "track")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<TrackActivityRecord> activities;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "track")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<TrackUsageRecord> supplies;

}

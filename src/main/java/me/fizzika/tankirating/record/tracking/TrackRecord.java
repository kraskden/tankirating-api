package me.fizzika.tankirating.record.tracking;

import lombok.*;
import me.fizzika.tankirating.record.IdRecord;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "track")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TrackRecord extends IdRecord {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    private int premium;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "track")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<TrackActivityRecord> activities;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "track")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<TrackSupplyRecord> supplies;

}

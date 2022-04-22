package me.fizzika.tankirating.record.tracking;

import lombok.*;
import me.fizzika.tankirating.record.IdRecord;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "track")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TrackRecord extends IdRecord {

    private UUID targetId;

    private LocalDateTime timestamp;

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
    @ToString.Exclude
    private List<TrackActivityRecord> activities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
    @ToString.Exclude
    private List<TrackSupplyRecord> supplies;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TrackRecord that = (TrackRecord) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

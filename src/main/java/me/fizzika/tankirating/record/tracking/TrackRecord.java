package me.fizzika.tankirating.record.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "track")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackRecord extends IdRecord {

    private UUID targetId;

    private LocalDateTime timestamp;

    private Integer gold;

    private Integer kills;

    private Integer deaths;

    private Integer cry;

    private Integer score;

    private Long time;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
    private List<TrackActivityRecord> activities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
    private List<TrackSupplyRecord> supplies;

}

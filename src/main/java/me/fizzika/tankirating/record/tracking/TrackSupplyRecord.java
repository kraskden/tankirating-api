package me.fizzika.tankirating.record.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "track_supply")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackSupplyRecord extends IdRecord {

    @ManyToOne
    @JoinColumn(name = "track_id")
    private TrackRecord track;

    private String name;

    private long usages;

}

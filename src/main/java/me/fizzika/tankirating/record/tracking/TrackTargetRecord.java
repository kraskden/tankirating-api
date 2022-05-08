package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@ToString
@Table(name = "target")
@NoArgsConstructor
public class TrackTargetRecord extends IdRecord {

    private String name;

    public TrackTargetRecord(String name) {
        this.name = name;
    }

}

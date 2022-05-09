package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "target")
@NoArgsConstructor
public class TrackTargetRecord extends IdRecord<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "target_seq_generator")
    @SequenceGenerator(name = "target_seq_generator", sequenceName = "target_seq", allocationSize = 1)
    private Integer id;

    private String name;

    public TrackTargetRecord(String name) {
        this.name = name;
    }

    public TrackTargetRecord(Integer id) {
        this.id = id;
    }

}

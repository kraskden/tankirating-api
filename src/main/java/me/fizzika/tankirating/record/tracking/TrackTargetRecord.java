package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
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

    @Enumerated(EnumType.STRING)
    private TrackTargetType type;

    @Enumerated(EnumType.STRING)
    private TrackTargetStatus status = TrackTargetStatus.ACTIVE;

    public TrackTargetRecord(String name, TrackTargetType type) {
        this.name = name;
        this.type = type;
    }

    public TrackTargetRecord(Integer id) {
        this.id = id;
    }

}

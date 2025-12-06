package me.fizzika.tankirating.record.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.record.IdRecord;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "entity")
@NoArgsConstructor
public class TrackEntityRecord extends IdRecord<Short> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq_generator")
    @SequenceGenerator(name = "entity_seq_generator", sequenceName = "entity_seq", allocationSize = 1)
    private Short id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TankiEntityType type;

}
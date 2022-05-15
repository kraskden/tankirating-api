package me.fizzika.tankirating.record.online;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.PeriodRecord;

import javax.persistence.*;

@Entity
@Table(name = "online_pcu")
@Getter
@Setter
@ToString
public class OnlinePcuRecord extends PeriodRecord<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "online_pcu_seq_generator")
    @SequenceGenerator(name = "online_pcu_seq_generator", sequenceName = "online_pcu_seq", allocationSize = 1)
    private Long id;

    private Integer onlinePcu;

    private Integer inbattlesPcu;

}

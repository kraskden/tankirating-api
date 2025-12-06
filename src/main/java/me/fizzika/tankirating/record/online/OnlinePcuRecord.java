package me.fizzika.tankirating.record.online;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.record.PeriodRecord;

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
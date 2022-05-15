package me.fizzika.tankirating.record.online;

import lombok.*;
import me.fizzika.tankirating.record.IdRecord;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "online_snapshot")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OnlineSnapshotRecord extends IdRecord<LocalDateTime> {

    @Id
    private LocalDateTime timestamp;

    private Integer online;

    private Integer inbattles;

    @Override
    public LocalDateTime getId() {
        return timestamp;
    }

    @Override
    public void setId(LocalDateTime id) {
        this.timestamp = id;
    }

}

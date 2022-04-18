package me.fizzika.tankirating.record;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class IdRecord {

    @Id
    private UUID id;

}

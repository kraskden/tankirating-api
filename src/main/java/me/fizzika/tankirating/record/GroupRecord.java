package me.fizzika.tankirating.record;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@Table(name = "group")
@EqualsAndHashCode(callSuper = true)
public class GroupRecord extends IdRecord {

    @Id
    private UUID id;

    private String name;

}

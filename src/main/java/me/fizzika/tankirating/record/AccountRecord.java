package me.fizzika.tankirating.record;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "account")
@EqualsAndHashCode(callSuper = true)
public class AccountRecord extends IdRecord {

    private String nickname;

}

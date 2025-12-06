package me.fizzika.tankirating.record;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import jakarta.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class IdRecord<I> {

    public abstract I getId();

    public abstract void setId(I id);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        return getId() != null && Objects.equals(getId(), ((IdRecord)o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
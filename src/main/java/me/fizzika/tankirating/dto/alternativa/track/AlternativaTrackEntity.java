package me.fizzika.tankirating.dto.alternativa.track;

import java.util.Objects;

/**
 * Base class for all items that are trackable.
 */
public abstract class AlternativaTrackEntity {

    public abstract String getName();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlternativaTrackEntity o)) {
            return false;
        }
        return this.getName().equals(o.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

}

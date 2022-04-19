package me.fizzika.tankirating.dto.alternativa.track;

import java.util.Objects;

/**
 * Base class for all items that are trackable.
 */
public abstract class AlternativaTrackEntity {

    public abstract String getId();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlternativaTrackEntity)) {
            return false;
        }
        AlternativaTrackEntity o = (AlternativaTrackEntity) obj;
        return this.getId().equals(o.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}

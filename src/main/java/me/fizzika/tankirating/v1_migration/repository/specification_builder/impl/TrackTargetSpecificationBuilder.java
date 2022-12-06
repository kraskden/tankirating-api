package me.fizzika.tankirating.v1_migration.repository.specification_builder.impl;

import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord_;
import me.fizzika.tankirating.v1_migration.repository.specification_builder.FilterSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

@Component
public class TrackTargetSpecificationBuilder implements FilterSpecificationBuilder<TrackTargetRecord, TrackTargetFilter> {

    @Override
    public Specification<TrackTargetRecord> filterToSpec(TrackTargetFilter filter) {
        return (root, query, cb) -> {
            Predicate result = cb.conjunction();

            if (filter.getQuery() != null) {
                result = cb.and(result, likeIgnoreCase(cb, root.get(TrackTargetRecord_.NAME), filter.getQuery()));
            }

            if (filter.getTargetType() != null) {
                result = cb.and(result, cb.equal(root.get(TrackTargetRecord_.TYPE), filter.getTargetType()));
            }

            if (filter.getStatuses() != null) {
                CriteriaBuilder.In<TrackTargetStatus> statusPredicate = cb.in(root.get(TrackTargetRecord_.STATUS));
                filter.getStatuses().forEach(statusPredicate::value);

                result = cb.and(result, statusPredicate);
            }

            return result;
        };
    }

}

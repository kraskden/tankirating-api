package me.fizzika.tankirating.v1_migration.repository.specification_builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public interface BaseSpecificationBuilder {

    default Predicate likeIgnoreCase(CriteriaBuilder cb, Expression<String> field, String query) {
        return cb.like(cb.lower(field), "%" + query.toLowerCase() + "%");
    }

}

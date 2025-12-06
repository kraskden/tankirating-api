package me.fizzika.tankirating.v1_migration.repository.specification_builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface BaseSpecificationBuilder {

    default Predicate likeIgnoreCase(CriteriaBuilder cb, Expression<String> field, String query) {
        return cb.like(cb.lower(field), "%" + query.toLowerCase() + "%");
    }

}
package me.fizzika.tankirating.v1_migration.repository.specification_builder;

import org.springframework.data.jpa.domain.Specification;

public interface FilterSpecificationBuilder<R, F> extends BaseSpecificationBuilder {

    Specification<R> filterToSpec(F filter);

}

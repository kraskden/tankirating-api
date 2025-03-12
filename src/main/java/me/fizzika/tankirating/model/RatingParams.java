package me.fizzika.tankirating.model;

import java.time.LocalDateTime;
import java.util.Set;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.date.DatePeriod;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

public record RatingParams(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable) {

    private static final Set<String> ALLOWED_SORTS = Set.of("trackRecord.kt", "trackRecord.kd", "trackRecord.cry", "trackRecord.score", "trackRecord.time");

    public DatePeriod getDatePeriod() {
        return period.getDatePeriod(LocalDateTime.now()).sub(offset);
    }

    public boolean isCacheable() {
        if (offset < 0 || offset > 1 || period == PeriodUnit.DAY) {
            return false;
        }
        if (!CollectionUtils.isEmpty(filter.getIds())) {
            return false;
        }
        if (pageable.getPageNumber() != 0) {
            return false;
        }
        if (pageable.getPageSize() != 25 && pageable.getPageSize() != 50) {
            return false;
        }

        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORTS.contains(order.getProperty())) {
                return false;
            }
        }

        return true;
    }
}
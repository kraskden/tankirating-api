package me.fizzika.tankirating.repository.online;

import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.record.online.OnlinePcuRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnlinePcuRepository extends JpaRepository<OnlinePcuRecord, Long> {

    Optional<OnlinePcuRecord> findFirstByPeriodAndPeriodStart(DiffPeriodUnit period, LocalDateTime periodStart);

    @Query("from OnlinePcuRecord R " +
            "where R.period = :periodUnit " +
            "and (cast(:from as timestamp) is null or R.periodStart >= :from) " +
            "and (cast(:to as timestamp) is null or R.periodStart <= :to) ")
    List<OnlinePcuRecord> findAllForPeriodAndRange(@Param("periodUnit") DiffPeriodUnit diffPeriodUnit,
                                                   @Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to,
                                                   Sort sort);

    default List<OnlinePcuRecord> findAllForPeriodAndRange(DiffPeriodUnit diffPeriodUnit, LocalDateTime from, LocalDateTime to) {
        return findAllForPeriodAndRange(diffPeriodUnit, from, to, Sort.by("periodStart"));
    }

}
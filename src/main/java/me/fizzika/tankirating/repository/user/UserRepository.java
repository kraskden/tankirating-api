package me.fizzika.tankirating.repository.user;

import me.fizzika.tankirating.dto.rating.UserRatingDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<TrackTargetRecord, Integer> {

    @Query("select new me.fizzika.tankirating.dto.rating.UserRatingDTO( " +
            "T.id, T.name, D.maxScore, TR.time, TR.kills, TR.deaths, TR.score, TR.cry) " +
            "from TrackDiffRecord D " +
            "left join D.trackRecord TR " +
            "left join D.target T " +
            "where T.status <> 'DISABLED' and D.period = :period and D.periodStart = :periodStart " +
            "and (:minScore is null or D.maxScore >= :minScore)")
    Page<UserRatingDTO> getRating(PeriodUnit period, LocalDateTime periodStart,
                                  Integer minScore,
                                  Pageable pageable);

}

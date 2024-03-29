package me.fizzika.tankirating.service.online.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.OnlinePeriodFilter;
import me.fizzika.tankirating.dto.online.OnlinePcuDTO;
import me.fizzika.tankirating.dto.online.OnlineSnapshotDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.OnlineMapper;
import me.fizzika.tankirating.repository.online.OnlinePcuRepository;
import me.fizzika.tankirating.repository.online.OnlineSnapshotRepository;
import me.fizzika.tankirating.service.online.OnlineService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    private final OnlineSnapshotRepository snapshotRepository;
    private final OnlinePcuRepository pcuRepository;
    private final OnlineMapper onlineMapper;

    @Override
    public List<OnlineSnapshotDTO> getSnapshots(OnlinePeriodFilter filter) {
        return snapshotRepository.findAllInRange(filter.getFrom(), filter.getTo(), Sort.by("timestamp")).stream()
                .map(onlineMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OnlineSnapshotDTO getLatestSnapshot() {
        return snapshotRepository.getLatest()
                .map(onlineMapper::toDto)
                .orElseThrow(() -> new ExternalException("Online snapshot is not exists", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<OnlinePcuDTO> getPcuPeriodData(PeriodUnit period, OnlinePeriodFilter periodFilter) {
        return pcuRepository.findAllForPeriodAndRange(period, periodFilter.getFrom(),
                        periodFilter.getTo()).stream()
                .map(onlineMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OnlinePcuDTO getPcuForPeriod(PeriodUnit period, Integer offset) {
        LocalDateTime start = period.getDatePeriod(LocalDateTime.now()).sub(offset).getStart();
        return pcuRepository.findFirstByPeriodAndPeriodStart(period, start)
                .map(onlineMapper::toDto)
                .orElseThrow(() -> new ExternalException(ExceptionType.PCU_RECORD_NOT_FOUND)
                        .arg("period", period)
                        .arg("offset", offset)
                );
    }

    @Override
    public List<OnlinePcuDTO> getCurrentPcuForAllPeriods() {
        var res = new ArrayList<OnlinePcuDTO>();
        for (PeriodUnit period : PeriodUnit.values()) {
            try {
                res.add(getPcuForPeriod(period, 0));
            } catch (ExternalException ex) {
                if (ExceptionType.PCU_RECORD_NOT_FOUND != ex.getType()) {
                    throw ex;
                }
            }
        }
        return res;
    }

}

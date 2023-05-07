package me.fizzika.tankirating.service.tracking.target;

import me.fizzika.tankirating.dto.filter.GroupStatFilter;
import me.fizzika.tankirating.dto.target.GroupStatDTO;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class GroupServiceImpl implements GroupService {

    @Resource
    private TrackTargetRepository targetRepository;

    @Override
    public GroupStatDTO getGroupStatistics(GroupMeta groupMeta, GroupStatFilter requestDTO) {
        LocalDateTime start = requestDTO.getFrom() == null ? null : requestDTO.getFrom().atStartOfDay();
        LocalDateTime end = requestDTO.getTo() == null ? null : requestDTO.getTo().atStartOfDay();
        long playedCount = targetRepository.getPlayedCount(groupMeta.getMinScore(), groupMeta.getMaxScore(),
                start, end);
        return new GroupStatDTO(groupMeta, playedCount);
    }
}

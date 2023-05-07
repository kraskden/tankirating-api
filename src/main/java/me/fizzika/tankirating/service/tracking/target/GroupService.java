package me.fizzika.tankirating.service.tracking.target;

import me.fizzika.tankirating.dto.target.GroupStatDTO;
import me.fizzika.tankirating.dto.filter.GroupStatFilter;
import me.fizzika.tankirating.enums.track.GroupMeta;

public interface GroupService {
    GroupStatDTO getGroupStatistics(GroupMeta groupMeta, GroupStatFilter requestDTO);
}

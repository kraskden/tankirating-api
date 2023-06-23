package me.fizzika.tankirating.service.management;

import java.util.List;
import me.fizzika.tankirating.dto.management.TargetStatEntryDTO;

public interface StatService {

    List<TargetStatEntryDTO> getTargetStat();
}

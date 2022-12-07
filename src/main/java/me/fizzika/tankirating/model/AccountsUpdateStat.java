package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AccountsUpdateStat {

    private long totalCount;
    private long processedCount;
    private List<TrackTargetDTO> retried = new ArrayList<>();

    public long getRetriedCount() {
        return retried.size();
    }

}

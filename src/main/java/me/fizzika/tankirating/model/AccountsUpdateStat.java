package me.fizzika.tankirating.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AccountsUpdateStat {

    private long totalCount;
    private long processedCount;
    private List<TrackTargetDTO> retried = new ArrayList<>();
    private Map<String, Integer> exceptionStats = new HashMap<>();

    public long getRetriedCount() {
        return retried.size();
    }

    public String toReportString() {
        String exceptionStats = getExceptionStats().entrySet()
                                                   .stream()
                                                   .map(e -> "%s: %d".formatted(e.getKey(), e.getValue()))
                                                   .collect(Collectors.joining("\n"));
        long totalExceptions = getExceptionStats().values()
                                                  .stream()
                                                  .reduce(0, Integer::sum);
        return """
                Total: %d; Processed: %d; Retried: %d;
                Exceptions: %d
                %s""".formatted(totalCount, processedCount, getRetriedCount(), totalExceptions, exceptionStats);
    }
}
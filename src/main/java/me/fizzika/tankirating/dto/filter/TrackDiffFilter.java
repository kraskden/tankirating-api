package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackDiffFilter extends TrackFormatFilter {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    private Integer offsetFrom;

    private Integer offsetTo;

    public TrackDatesFilter toDatesFilter() {
        var res = new TrackDatesFilter();
        res.format = format;
        res.setFrom(from);
        res.setTo(to);
        return res;
    }

    public TrackOffsetFilter toOffsetFilter() {
        var res = new TrackOffsetFilter();
        res.format = format;
        res.setOffsetFrom(offsetFrom);
        res.setOffsetTo(offsetTo);
        return res;
    }

}

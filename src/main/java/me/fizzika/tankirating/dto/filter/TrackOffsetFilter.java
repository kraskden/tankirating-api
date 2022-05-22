package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackOffsetFilter extends TrackFormatFilter {

    private Integer offsetFrom;

    private Integer offsetTo;

}

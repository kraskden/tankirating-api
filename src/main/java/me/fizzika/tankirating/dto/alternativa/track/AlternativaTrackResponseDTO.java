package me.fizzika.tankirating.dto.alternativa.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlternativaTrackResponseDTO {

    private String responseType;

    @JsonProperty("response")
    private AlternativaTrackDTO track;

}

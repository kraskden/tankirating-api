package me.fizzika.tankirating.dto.alternativa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlternativaTrackResponseDTO {

    private String responseType;

    @JsonProperty("response")
    private AlternativaTrackDTO track;

}

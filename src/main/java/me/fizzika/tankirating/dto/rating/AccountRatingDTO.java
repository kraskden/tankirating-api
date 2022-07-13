package me.fizzika.tankirating.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRatingDTO {

    private Integer id;
    private String name;
    private Integer maxScore;

    private Long time;
    private Integer kills;
    private Integer deaths;
    private Integer score;
    private Integer cry;

}

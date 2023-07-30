package me.fizzika.tankirating.dto.rating;

import java.util.List;
import lombok.Data;

@Data
public class RatingFilter {

    private Integer minScore;
    private List<Integer> ids;
}
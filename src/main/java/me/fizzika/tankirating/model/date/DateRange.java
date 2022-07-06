package me.fizzika.tankirating.model.date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {

    protected LocalDateTime start;

    protected LocalDateTime end;

}

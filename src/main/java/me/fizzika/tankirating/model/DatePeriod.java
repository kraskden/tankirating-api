package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DatePeriod {

    private LocalDateTime start;

    private LocalDateTime end;

}

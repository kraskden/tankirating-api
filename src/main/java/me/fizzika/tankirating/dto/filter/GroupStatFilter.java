package me.fizzika.tankirating.dto.filter;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupStatFilter {
    private LocalDate from;
    private LocalDate to;
}

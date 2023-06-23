package me.fizzika.tankirating.dto.management;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RegisterStatEntryDTO {

    private LocalDate date;
    private Long registrations;
}

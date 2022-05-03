package me.fizzika.tankirating.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.UUID;

@Data
public class AccountDTO {

    @Null
    private UUID id;

    @NotBlank
    private String name;

}

package me.fizzika.tankirating.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AccountDTO {

    @Null
    private UUID id;

    @NotBlank
    private String name;

    public AccountDTO(String name) {
        this.name = name;
    }

}

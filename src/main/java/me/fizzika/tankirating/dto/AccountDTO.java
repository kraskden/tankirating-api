package me.fizzika.tankirating.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
public class AccountDTO {

    @Null
    private Integer id;

    @NotBlank
    private String name;

    public AccountDTO(String name) {
        this.name = name;
    }

}

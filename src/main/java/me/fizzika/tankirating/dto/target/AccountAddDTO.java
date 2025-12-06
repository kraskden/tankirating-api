package me.fizzika.tankirating.dto.target;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class AccountAddDTO {

    @Size(min = 1, max = 10)
    private List<String> nicknames;

}
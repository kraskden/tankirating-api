package me.fizzika.tankirating.dto.target;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.AccountAddStatus;

@Data
@AllArgsConstructor
public class AccountAddResultDTO {

    private String nickname;

    private AccountAddStatus status;

}

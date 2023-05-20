package me.fizzika.tankirating.dto.target;

import lombok.Data;

@Data
public class AccountUpdateResultDTO {
    private TrackTargetDTO account;
    private boolean processed;
}

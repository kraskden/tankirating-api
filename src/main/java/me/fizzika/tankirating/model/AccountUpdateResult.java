package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.dto.TrackTargetDTO;

@Data
@AllArgsConstructor
public class AccountUpdateResult {

    private TrackTargetDTO account;
    private boolean processed;

    public static AccountUpdateResult processed(TrackTargetDTO account) {
        return new AccountUpdateResult(account, true);
    }

    public static AccountUpdateResult retrying(TrackTargetDTO account) {
        return new AccountUpdateResult(account, false);
    }

}

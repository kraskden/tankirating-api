package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;

@Data
@AllArgsConstructor
public class AccountUpdateResult {

    private TrackTargetDTO account;
    private boolean processed;

    private Throwable throwable;
    private String exceptionClass;

    public static AccountUpdateResult failed(TrackTargetDTO account, String exceptionClass) {
        return new AccountUpdateResult(account, true, null, exceptionClass);
    }

    public static AccountUpdateResult failed(TrackTargetDTO account, Throwable ex) {
        return new AccountUpdateResult(account, true, ex, ex.getClass().getSimpleName());
    }

    public static AccountUpdateResult ok(TrackTargetDTO account) {
        return new AccountUpdateResult(account, true, null, null);
    }

    public static AccountUpdateResult retrying(TrackTargetDTO account) {
        return new AccountUpdateResult(account, false, null, null);
    }
}
package me.fizzika.tankirating.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {
    TRACK_TARGET_ALREADY_EXISTS("Track target already exists in the system", HttpStatus.CONFLICT),

    ACCOUNT_SNAPSHOT_NOT_FOUND("Account snapshot is not found", HttpStatus.NOT_FOUND),
    ACCOUNT_DIFF_NOT_FOUND("Account diff not found", HttpStatus.NOT_FOUND),

    ACCOUNT_NOT_FOUND("Account not exists in the system", HttpStatus.NOT_FOUND),
    ACCOUNT_ALREADY_EXISTS("Account already exists in the system", HttpStatus.CONFLICT),
    ALTERNATIVA_ACCOUNT_NOT_FOUND("Account not exists in the ratings.tankionline.com", HttpStatus.NOT_FOUND)
    ;

    private final String message;
    private final HttpStatus status;

}

package me.fizzika.tankirating.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    TRACK_TARGET_ALREADY_EXISTS("Track target already exists in the system", HttpStatus.CONFLICT),

    TRACK_SNAPSHOT_NOT_FOUND("Track snapshot is not found", HttpStatus.NOT_FOUND),
    TRACK_DIFF_NOT_FOUND("Track diff not found", HttpStatus.NOT_FOUND),
    TRACK_TARGET_NOT_FOUND("Track target not exists in the system", HttpStatus.NOT_FOUND),

    ALTERNATIVA_ACCOUNT_NOT_FOUND("Account not exists in the ratings.tankionline.com", HttpStatus.NOT_FOUND)
    ;

    private final String message;
    private final HttpStatus status;

}

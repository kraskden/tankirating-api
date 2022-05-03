package me.fizzika.tankirating.exceptions;

import org.springframework.http.HttpStatus;

public class ServerException extends ExternalException {
    public ServerException(String msg) {
        super(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

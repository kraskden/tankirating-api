package me.fizzika.tankirating.exceptions;


import lombok.Getter;
import me.fizzika.tankirating.enums.ExceptionType;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ExternalException extends RuntimeException {

    private final String name;

    private final HttpStatus httpStatus;

    private final Map<String, Object> args = new HashMap<>();

    public ExternalException(String msg, HttpStatus status) {
        super(msg);
        this.name = null;
        this.httpStatus = status;
    }

    public ExternalException(ExceptionType exType) {
        super(exType.getMessage());
        this.name = exType.name();
        this.httpStatus = exType.getStatus();
    }

    public ExternalException arg(String k, Object v) {
        args.put(k, v);
        return this;
    }

    public ExternalException args(Map<String, Object> args) {
        this.args.putAll(args);
        return this;
    }

}

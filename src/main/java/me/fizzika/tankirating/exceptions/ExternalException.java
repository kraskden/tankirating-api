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

    private final ExceptionType type;

    public ExternalException(String msg, HttpStatus status) {
        super(msg);
        this.name = null;
        this.httpStatus = status;
        this.type = null;
    }

    public ExternalException(ExceptionType exType) {
        super(exType.getMessage());
        this.type = exType;
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

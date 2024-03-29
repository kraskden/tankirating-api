package me.fizzika.tankirating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.exceptions.ExternalException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ExceptionDTO {

    private String message;
    private String name;
    private HttpStatus status;
    private Map<String, Object> args = new HashMap<>();

    public ExceptionDTO(ExternalException ex) {
        message = ex.getMessage();
        name = ex.getName();
        status = ex.getHttpStatus();
        args = ex.getArgs();
    }

    public ExceptionDTO(HttpStatus status, String message) {
        name = status.name();
        this.message = message;
        this.status = status;
    }

    public int getCode() {
        return status.value();
    }

}

package me.fizzika.tankirating.config;

import me.fizzika.tankirating.dto.ExceptionDTO;
import me.fizzika.tankirating.exceptions.ExternalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ExternalException.class)
    public ResponseEntity<ExceptionDTO> handleExternalException(ExternalException ex) {
        return new ResponseEntity<>(new ExceptionDTO(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionDTO> handleBindException(BindException ex) {
        ExceptionDTO dto = new ExceptionDTO();
        dto.setStatus(HttpStatus.BAD_REQUEST);
        dto.setMessage(ex.getMessage());
        return new ResponseEntity<>(dto, dto.getStatus());
    }

}

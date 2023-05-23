package me.fizzika.tankirating.config;

import me.fizzika.tankirating.dto.ExceptionDTO;
import me.fizzika.tankirating.exceptions.ExternalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ExternalException.class)
    public ResponseEntity<ExceptionDTO> handleExternalException(ExternalException ex) {
        return new ResponseEntity<>(new ExceptionDTO(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionDTO> handleBindException(BindException ex) {
        var dto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(dto, dto.getStatus());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(AuthenticationException ex) {
        var dto = new ExceptionDTO(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(dto, dto.getStatus());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ExceptionDTO> handleAccessDeniedException(AccessDeniedException ex) {
        var dto = new ExceptionDTO(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<>(dto, dto.getStatus());
    }
}

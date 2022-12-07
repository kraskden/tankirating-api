package me.fizzika.tankirating.exceptions.alternativa;

public class AlternativaTooManyRequestsException extends AlternativaException {

    public AlternativaTooManyRequestsException() {
    }

    public AlternativaTooManyRequestsException(String message) {
        super(message);
    }

    public AlternativaTooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlternativaTooManyRequestsException(Throwable cause) {
        super(cause);
    }
}

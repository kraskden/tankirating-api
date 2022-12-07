package me.fizzika.tankirating.exceptions.alternativa;

public class AlternativaException extends RuntimeException {

    public AlternativaException() {
    }

    public AlternativaException(String message) {
        super(message);
    }

    public AlternativaException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlternativaException(Throwable cause) {
        super(cause);
    }

}

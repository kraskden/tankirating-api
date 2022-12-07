package me.fizzika.tankirating.exceptions.alternativa;

public class AlternativaUserNotFoundException extends AlternativaException {
    public AlternativaUserNotFoundException() {
    }

    public AlternativaUserNotFoundException(String message) {
        super(message);
    }

    public AlternativaUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlternativaUserNotFoundException(Throwable cause) {
        super(cause);
    }
}

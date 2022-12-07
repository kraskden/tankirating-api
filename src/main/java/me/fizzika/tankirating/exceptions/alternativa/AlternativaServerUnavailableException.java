package me.fizzika.tankirating.exceptions.alternativa;

public class AlternativaServerUnavailableException extends AlternativaException {

    public AlternativaServerUnavailableException() {
    }

    public AlternativaServerUnavailableException(String message) {
        super(message);
    }

    public AlternativaServerUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlternativaServerUnavailableException(Throwable cause) {
        super(cause);
    }

}

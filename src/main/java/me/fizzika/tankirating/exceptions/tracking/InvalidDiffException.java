package me.fizzika.tankirating.exceptions.tracking;

public class InvalidDiffException extends RuntimeException {
    public InvalidDiffException(String message) {
        super(message);
    }

    public InvalidDiffException() {
    }
}

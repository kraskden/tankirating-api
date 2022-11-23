package me.fizzika.tankirating.exceptions.tracking;

public class InvalidTrackDataException extends RuntimeException {
    public InvalidTrackDataException() {
    }

    public InvalidTrackDataException(String message) {
        super(message);
    }

    public InvalidTrackDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTrackDataException(Throwable cause) {
        super(cause);
    }
}

package one.global.api.domain.exception;

public class InvalidDeviceParameter extends RuntimeException{
    public InvalidDeviceParameter(String message) {
        super(message);
    }

    public InvalidDeviceParameter(String message, Throwable cause) {
        super(message, cause);
    }
}

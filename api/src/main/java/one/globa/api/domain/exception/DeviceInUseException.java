package one.globa.api.domain.exception;

public class DeviceInUseException extends RuntimeException {

    public DeviceInUseException(String message) {
        super(message);
    }

    public DeviceInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}

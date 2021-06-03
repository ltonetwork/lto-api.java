package legalthings.lto_api.lto.exceptions;

@SuppressWarnings("serial")
public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}

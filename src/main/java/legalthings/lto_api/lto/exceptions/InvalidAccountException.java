package legalthings.lto_api.lto.exceptions;

@SuppressWarnings("serial")
public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}

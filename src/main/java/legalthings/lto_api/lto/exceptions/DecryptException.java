package legalthings.lto_api.lto.exceptions;

@SuppressWarnings("serial")
public class DecryptException extends RuntimeException {
    public DecryptException(String message) {
        super(message);
    }
}

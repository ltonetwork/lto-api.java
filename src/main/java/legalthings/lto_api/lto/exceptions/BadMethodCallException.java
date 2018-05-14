package legalthings.lto_api.lto.exceptions;

@SuppressWarnings("serial")
public class BadMethodCallException extends RuntimeException {
	public BadMethodCallException(String message) {
		super(message);
	}
}
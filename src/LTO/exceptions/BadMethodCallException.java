/**
 * Failed to decrypt message
 */
package LTO.exceptions;

/**
 * @author moonbi
 *
 */
@SuppressWarnings("serial")
public class BadMethodCallException extends RuntimeException {
	public BadMethodCallException(String message) {
		super(message);
	}
}

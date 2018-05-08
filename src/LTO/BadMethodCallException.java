/**
 * Failed to decrypt message
 */
package LTO;

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

/**
 * Failed to decrypt message
 */
package LTO.exceptions;

/**
 * @author moonbi
 *
 */
@SuppressWarnings("serial")
public class InvalidArgumentException extends RuntimeException {
	public InvalidArgumentException(String message) {
		super(message);
	}
}

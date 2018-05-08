/**
 * Failed to decrypt message
 */
package LTO.exceptions;

/**
 * @author moonbi
 *
 */
@SuppressWarnings("serial")
public class InvalidAccountException extends RuntimeException {
	public InvalidAccountException(String message) {
		super(message);
	}
}

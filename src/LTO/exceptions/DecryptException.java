/**
 * Failed to decrypt message
 */
package LTO.exceptions;

/**
 * @author moonbi
 *
 */
@SuppressWarnings("serial")
public class DecryptException extends RuntimeException {
	public DecryptException(String message) {
		super(message);
	}
}

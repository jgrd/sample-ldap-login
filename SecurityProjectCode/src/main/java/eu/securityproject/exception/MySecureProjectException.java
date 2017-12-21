package eu.securityproject.exception;

/**
 * Created by Letizia Vitari on 20/12/17.
 */

@SuppressWarnings("serial")
public class MySecureProjectException extends RuntimeException {

	public MySecureProjectException(String message) {
		super(message);
	}
	
	public MySecureProjectException(String message, Throwable cause) {
		super(message, cause);
	}

}

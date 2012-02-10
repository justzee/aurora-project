/**
 * Created on: 2002-11-15 23:09:54
 * Author:     zhoufan
 */
package org.lwap.application;

/**
 * 
 */
public class ApplicationInitializeException extends Exception {

	/**
	 * Constructor for ApplicationInitializeException.
	 */
	public ApplicationInitializeException() {
		super();
	}

	/**
	 * Constructor for ApplicationInitializeException.
	 * @param message
	 */
	public ApplicationInitializeException(String message) {
		super(message);
	}

	/**
	 * Constructor for ApplicationInitializeException.
	 * @param message
	 * @param cause
	 */
	public ApplicationInitializeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for ApplicationInitializeException.
	 * @param cause
	 */
	public ApplicationInitializeException(Throwable cause) {
		super(cause);
	}

}

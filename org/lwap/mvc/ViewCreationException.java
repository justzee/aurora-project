/*
 * BuildException.java
 *
 * Created on 2002年1月13日, 上午1:13
 */

package org.lwap.mvc;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ViewCreationException extends Exception {


    
	/**
	 * Constructor for ViewCreationException.
	 */
	public ViewCreationException() {
		super();
	}

	/**
	 * Constructor for ViewCreationException.
	 * @param message
	 */
	public ViewCreationException(String message) {
		super(message);
	}

	/**
	 * Constructor for ViewCreationException.
	 * @param message
	 * @param cause
	 */
	public ViewCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for ViewCreationException.
	 * @param cause
	 */
	public ViewCreationException(Throwable cause) {
		super(cause);
	}

}



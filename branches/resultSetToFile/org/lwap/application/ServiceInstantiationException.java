/**
 * Created on: 2002-12-13 18:17:13
 * Author:     zhoufan
 */
package org.lwap.application;


/**
 * 
 */
public class ServiceInstantiationException extends Exception {

	/**
	 * Constructor for ServiceInstantiationException.
	 */
	public ServiceInstantiationException() {
		super();
	}

	/**
	 * Constructor for ServiceInstantiationException.
	 * @param arg0
	 */
	public ServiceInstantiationException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor for ServiceInstantiationException.
	 * @param arg0
	 * @param arg1
	 */
	public ServiceInstantiationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor for ServiceInstantiationException.
	 * @param arg0
	 */
	public ServiceInstantiationException(Throwable arg0) {
		super(arg0);
	}

}

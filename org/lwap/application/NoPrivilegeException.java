/**
 * Created on: 2002-12-12 13:21:52
 * Author:     zhoufan
 */
package org.lwap.application;

import javax.servlet.ServletException;

/**
 * 
 */
public class NoPrivilegeException extends ServletException {

	/**
	 * Constructor for NoPrivilegeException.
	 */
	public NoPrivilegeException() {
		super();
	}

	/**
	 * Constructor for NoPrivilegeException.
	 * @param arg0
	 */
	public NoPrivilegeException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor for NoPrivilegeException.
	 * @param arg0
	 * @param arg1
	 */
	public NoPrivilegeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor for NoPrivilegeException.
	 * @param arg0
	 */
	public NoPrivilegeException(Throwable arg0) {
		super(arg0);
	}

}

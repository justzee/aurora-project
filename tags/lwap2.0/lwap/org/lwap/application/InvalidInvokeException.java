/**
 * Created on: 2002-11-11 17:51:46
 * Author:     zhoufan
 */
package org.lwap.application;

/**
 * 
 */
public class InvalidInvokeException extends Exception {
	
	public InvalidInvokeException(){
		super();	
	}
	
	public InvalidInvokeException( String message){
		super(message);
	}
	
	public InvalidInvokeException(Throwable cause){
		super(cause);
	}

}

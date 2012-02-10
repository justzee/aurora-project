/**
 * Created on: 2002-11-13 14:12:28
 * Author:     zhoufan
 */
package org.lwap.ui;

/**
 * For component that is capable of prompt some message
 * 
 * A prompt is usually some text message displayed near the component
 * 
 * A hint is usually some text message displayed when user focus on it for a while
 * and hides on user leave
 */
public interface PromptPossess {

	public String  getPrompt();
	
	public void   setPrompt( String prompt );
	
	public String getHint();
	
	public void   setHint( String hint);
}

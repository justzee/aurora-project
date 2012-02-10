/**
 * Created on: 2002-12-27 14:36:32
 * Author:     zhoufan
 */
package org.lwap.mvc;

/** Interface used by BuildSession to get localized string.
 *  Assumed that instance of LocalizedStringProvider already contains
 *  locale information from client session 
 */
public interface LocalizedStringProvider {
	
	/** get localized string by key */
	public String getLocalizedString( String key );

}

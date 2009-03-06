/**
 * Created on: 2002-11-18 15:31:04
 * Author:     zhoufan
 */
package org.lwap.application;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 */
public interface ResourceBundleFactory {
	
	
	/**
	 * get resource bundle for specified locale
	 * @param locale java.util.Locale instance
	 * @return ResourceBundle for specified Locale. If no entry found,
	 * the default locale should be returned.
	 */
	public ResourceBundle getResourceBundle( Locale locale);

}

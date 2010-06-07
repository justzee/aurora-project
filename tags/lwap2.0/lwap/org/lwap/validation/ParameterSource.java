/**
 * Created on: 2002-11-14 15:27:02
 * Author:     zhoufan
 */
package org.lwap.validation;

import java.util.Collection;

public interface ParameterSource {
	
	public String getParameter( String key);
	
	public Collection getParameters( String key);
	
	public String[] getParameterArray( String key);

}

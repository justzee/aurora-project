/**
 * Created on: 2002-11-25 10:59:03
 * Author:     zhoufan
 */

package org.lwap.application;

import java.io.IOException;

import javax.servlet.ServletException;



/**
 *  For Non-UI Service
 */
public class NonUIService extends BaseService {
	
	public void createView() throws IOException,ServletException
	{
		super.setViewOutput(false);
	}	

}

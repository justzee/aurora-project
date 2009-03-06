/**
 * Created on: 2002-11-25 10:45:19
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;

import javax.servlet.ServletException;

import uncertain.composite.CompositeMap;


public class ViewTester extends BaseService {

	public void doService() throws  IOException,ServletException{
	}	
	
	public void createModel() throws  IOException,ServletException{
		CompositeMap m = this.getModelConfig();
		if( m != null)
			this.getModel().addChilds(m.getChilds());
	}
	

}

/**
 * Created on: 2002-11-11 15:32:42
 * Author:     zhoufan
 */
package org.lwap.application.sample;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.application.BaseService;

import uncertain.composite.CompositeMap;

public class HelloWorld extends BaseService {

	public void createView(HttpServletRequest request, HttpServletResponse response )
				  throws IOException, ServletException{
		
		response.setContentType("text/html");		  					  	
		CompositeMap view = getView();
//		CompositeMap default_page = view.getChild("default_page");
		CompositeMap label = view.createChild(null,null,"label");
		label.put("text", "Hello,world!");
		
	}
}

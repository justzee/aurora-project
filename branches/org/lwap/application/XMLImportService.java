/**
 * Created on: 2003-11-24 15:20:27
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;

import org.lwap.mvc.ViewCreationException;
import org.xml.sax.SAXParseException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;

/**
 * 
 */
public class XMLImportService extends XMLCheckedService {
    
    CompositeLoader loader = new CompositeLoader();
	
	void printErrorResponse( CompositeMap error) throws IOException {
		response.setContentType("text/xml");
		response.setLocale(Locale.CHINESE);
		PrintWriter out = response.getWriter();
		out.write("<?xml version=\"1.0\" encoding=\"gbk\" ?>");
		out.write(error.toXML());
	}
	

	/**
	 * @see org.lwap.application.BaseService#doService()
	 */
	public void doService() throws IOException, ServletException {
		CompositeMap context = getServiceContext();
		InputStream stream   = null;
		CompositeMap action_conf = getActionConfig();
		if(action_conf == null) return;

		try{
			stream = request.getInputStream();
			CompositeMap content = loader.loadFromStream(stream);			
			if( content != null){
				//System.out.println(content.toXML());
				//Iterator it = content.getChildIterator();
				//while( it.hasNext()){
				//	CompositeMap record = (CompositeMap) it.next();
					context.addChild(content);
					databaseAccess( action_conf, content, getModel());
				//}
			}
		} catch(Throwable ex){	
            System.out.println(ex.getMessage());
			if ( ex instanceof SAXParseException){
				SAXParseException sex = (SAXParseException)ex;
				System.out.println("line:"+sex.getLineNumber()+" row:"+sex.getColumnNumber());
			}
			CompositeMap error = new CompositeMap("error");
			error.putString("message",ex.getMessage());
			printErrorResponse(error);
		} finally{
			if( stream != null) stream.close();
		}
	}
	
	

	/**
	 * @see org.lwap.application.BaseService#buildOutputContent()
	 */
	public void buildOutputContent()
		throws IOException, ViewCreationException, ServletException {
		//super.buildOutputContent();
	}

}

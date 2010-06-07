/*
 * Created on 2005-10-30
 */
package org.lwap.feature;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;


/**
 * XmlOutput
 * @author Zhou Fan
 * 
 */
public class XmlOutput extends ModelTextOutput {
    
    public static final String DEFAULT_XML_CONTENT_TYPE = "text/xml;charset=utf-8";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    
    public String Case;

    /**
     * @param engine
     */
    public XmlOutput() {
        
    }
    
    public String getDefaultContentType(){
        return DEFAULT_XML_CONTENT_TYPE;
    }

    public void writeOutput(HttpServletResponse response, CompositeMap model) throws IOException {
        
        if(model==null) return;
		PrintWriter writer = response.getWriter();
		writer.print("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		writer.println();
		String output = model.toXML();
		if(Case!=null){
			if("lower".equalsIgnoreCase(Case)){
			    output = output.toLowerCase();
			}else if("upper".equalsIgnoreCase(Case)){
			    output = output.toUpperCase();
			}
    	}
	    writer.write(output);
    }

}

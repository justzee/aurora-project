/*
 * Created on 2007-6-11
 */
package org.lwap.feature;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.proc.ProcedureRunner;

public class JsonOutput extends ModelTextOutput {

    public static final String DEFAULT_JSON_CONTENT_TYPE = "text/html;charset=utf-8";
    
    public JsonOutput(){
        
    }

    public String getDefaultContentType() {
        return DEFAULT_JSON_CONTENT_TYPE;
    }

    public void writeOutput(HttpServletResponse response, CompositeMap model)
            throws IOException {
    	response.setHeader("Cache-Control","no-cache, must-revalidate");
        Writer out = response.getWriter();
        JSONObject obj = JSONAdaptor.toJSONObject(model);
        try{
            obj.write(out);        
        }catch(JSONException ex){
            throw new IOException(ex.getMessage());
        }
    }
    
    public int onCreateSuccessResponse(ProcedureRunner runner)
        throws IOException
    {
        return onBuildOutputContent(runner);
    }

}

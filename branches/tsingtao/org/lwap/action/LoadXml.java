/*
 * Created on 2007-6-28
 */
package org.lwap.action;

import org.lwap.application.WebApplication;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class LoadXml extends AbstractEntry {
    
    WebApplication application;
    
    public String   Source_file;
    public String   Source_path;
    public String   Destination;
    public boolean  Collapse = false;
    
    public LoadXml(WebApplication app){
        this.application = app;
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap context = runner.getContext();
        CompositeLoader loader = application.getCompositeLoader();
        CompositeMap result = null;
        if(Source_file!=null){
            Source_file=TextParser.parse(Source_file, context);
            result = loader.loadByFile(Source_file);
        }
        else if(Source_path!=null){
            Source_path=TextParser.parse(Source_path, context);
            result = loader.loadFromClassPath(Source_path);
        }
        else
            throw new ConfigurationError("Must set either 'source_file' or 'source_path'");
        if(result!=null){
            if(Collapse)
                CompositeUtil.collapse(result);
            context.putObject(Destination, result, true);
        }else{
            throw new IllegalArgumentException("[LoadXml] Can't load '"+(Source_file==null?Source_path:Source_file)+"'");
        }
    }

}

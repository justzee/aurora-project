/*
 * Created on 2009-5-26
 */
package org.lwap.action;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CompositeMapSave extends AbstractEntry {
    
    String      sourcePath;
    String      file;

    public void run(ProcedureRunner runner) throws Exception {
        ILogger logger = runner.getLogger();
        if( sourcePath==null ) throw new ConfigurationError("sourcePath property must be set");
        if( file==null ) throw new ConfigurationError("file property must be set");
        CompositeMap map = (CompositeMap)runner.getContext().getObject(sourcePath);
        if(map==null){
            logger.info("");
        }
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

}

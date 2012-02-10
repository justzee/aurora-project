/*
 * Created on 2008-12-1
 */
package org.lwap.feature;

import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.event.EventModel;

public class SqlExecutor extends AbstractServiceHandle {
    
    String  mFile;
    
    public SqlExecutor(){
        super();
    }

    public int handleEvent(int sequence, CompositeMap context, Object[] parameters) 
        throws Exception 
    {
        if(mFile==null) throw new ConfigurationError("Must set 'file' property to specify a sql script config file");
        MainService service = MainService.getServiceInstance(context);
        service.databaseAccess(mFile, service.getParameters(), service.getModel());
        return EventModel.HANDLE_NORMAL;
    }


    /**
     * @return the file
     */
    public String getFile() {
        return mFile;
    }


    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.mFile = file;
    }

}

/*
 * Created on 2008-11-2
 */
package org.lwap.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwap.application.WebApplication;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ServiceFileInfo extends AbstractEntry {
    
    public void run(ProcedureRunner runner)  throws Exception {
        MainService svc = MainService.getServiceInstance(runner.getContext());
        CompositeMap params = svc.getParameters();
        String service = params.getString("service");
        if(service!=null){
            CompositeLoader loader = ((WebApplication)svc.getApplication()).getCompositeLoader();
            CompositeMap file_map = svc.getModel().createChild("service-file");
            File file = loader.getFile(service);
            if(file!=null){
                file_map.put("exists", new Boolean(true));
                file_map.put("path", file.getPath());
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                file_map.put("date", date_format.format( new Date(file.lastModified())) );
                file_map.put("size", Long.toString(file.length()));
            }else{
                file_map.put("exists", new Boolean(false));
            }
        }
    }

}

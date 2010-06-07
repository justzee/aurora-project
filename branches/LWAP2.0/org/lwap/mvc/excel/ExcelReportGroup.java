/*
 * Created on 2009-9-25 下午05:08:36
 * Author: Zhou Fan
 */
package org.lwap.mvc.excel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwap.application.Service;
import org.lwap.application.WebApplication;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

public class ExcelReportGroup implements IFeature{
    
    String          source;
    String          service;
    MainService     serviceInstance;
    CompositeMap    context;
    CompositeMap    service_list;
    CompositeMap    excel_report_group_config;
    List            model_config_list = new LinkedList();
    List            view_config_list = new LinkedList();
    // excel-report that contains excel-report-group
    CompositeMap    parent_view_config;
    ILogger         logger;
    
    void loadModelAndViewList()
        throws Exception
    {
        WebApplication app = (WebApplication)serviceInstance.getApplication();
        Configuration config = serviceInstance.getConfiguration();
        if(source==null)
            throw new ConfigurationError("'source' must be set");
        if(service==null)
            throw new ConfigurationError("'service' must be set");
        CompositeMap srcMap = (CompositeMap)context.getObject(source);
        if(srcMap==null){
            logger.info("Can't get service list from path "+source);
            return;
        }
        List childs = srcMap.getChilds();
        if( childs==null){
            logger.info("Service list from path '"+source+"'is empty");
            return;
        }
        for(Iterator it = childs.iterator(); it.hasNext(); ){
            CompositeMap item = (CompositeMap)it.next();
            Object obj = item.getObject(service);
            String svc_name = obj==null?null:obj.toString();
            if(svc_name==null){
                logger.info("Can't get service from parameter "+item.toXML());
                continue;
            }
            Service svc = app.getService(svc_name);
            if(svc==null)
                throw new IllegalArgumentException("Can't load service "+svc_name);
            CompositeMap service_config = svc.getServiceConfig();            
            // load model config
            CompositeMap service_model = service_config.getChild("model");
            if(service_model!=null)
                if(service_model.getChilds()!=null){
                    model_config_list.addAll(service_model.getChilds());
                    //config.loadConfigList(service_model.getChilds());
                    logger.config("Add model config from "+svc_name+", "+service_model.getChilds().size()+" items added");                    
                }
            // load view config
            CompositeMap service_view = service_config.getChild("view");
            if(service_view!=null){
                CompositeMap excel_report = CompositeUtil.findChild(service_view, "excel-report");
                if(excel_report==null){
                    logger.info("service "+svc_name+" doesn't contains <excel-report> in view");
                    continue;
                }
                view_config_list.addAll(excel_report.getChilds());
                //config.loadConfigList(excel_report.getChilds());
                logger.config("Add view config from "+svc_name+", "+view_config_list.size()+" items added");
            }            
        }
        //serviceInstance.reloadConfig();
        logger.config("Participants after merge:"+config.getParticipantList());
        //logger.config("Instance map:"+config.getInstanceMap());
    }
    
    public void onBeginService( ProcedureRunner runner ){
        context = runner.getContext();
        serviceInstance = MainService.getServiceInstance(context);
        logger = LoggingContext.getLogger(context);
    }
    
    public void onPrepareService( ProcedureRunner runner ) throws Exception {
        loadModelAndViewList();
        CompositeMap model_config = serviceInstance.getModelConfig();
        if(model_config==null)
            model_config = serviceInstance.getServiceConfig().createChild("model");
        model_config.addChilds(model_config_list);
        serviceInstance.getConfiguration().loadConfigList(model_config.getChilds());
        parent_view_config.removeChild(excel_report_group_config);
        parent_view_config.addChilds(view_config_list);
        logger.config("Service config after merge:"+serviceInstance.getServiceConfig().toXML());
        //runner.stop();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int attachTo(CompositeMap config, Configuration procConfig) {
        excel_report_group_config = config;
        parent_view_config = config.getParent();
        return IFeature.NORMAL;
    }
    
    public int preParseParameter()
        throws Exception
    {
        serviceInstance.onParseParameter();
        return EventModel.HANDLE_STOP;
    }

}

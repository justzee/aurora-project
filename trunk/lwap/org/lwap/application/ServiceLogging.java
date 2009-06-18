/*
 * Created on 2009-4-21
 */
package org.lwap.application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Handler;

import org.lwap.controller.MainService;
import org.lwap.database.DatabaseAccess;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.DirectoryConfig;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.ConfigurableLoggerProvider;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggerProviderGroup;
import uncertain.logging.LoggerProviderGroup;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.OCManager;

public class ServiceLogging extends ConfigurableLoggerProvider implements 
    IGlobalInstance, IContextListener, IConfigurable 
{
    
    private static final String SERVICE_LOGGING_FILE = "SERVICE_LOGGING_FILE";
    UncertainEngine mEngine;
    DirectoryConfig mDirConfig;
    OCManager       mOcManager;
    String          mPattern;
    CompositeMap    mConfig;
    // file name -> BasicFileHandler
    HashMap         mHandlerMap;
    boolean         mAppend;
    
    public ServiceLogging(UncertainEngine   engine){
        super();
        mEngine = engine;
        mOcManager = engine.getOcManager();
        mHandlerMap = new HashMap();
        mDirConfig = engine.getDirectoryConfig();
        //System.out.println("creating service logging config");
    }
    
    public String getLogFilePath( String prefix, CompositeMap context ){
        if(mPattern!=null) prefix = prefix + TextParser.parse(mPattern, context);  
        return prefix;
    }
    
    public String getLogFilePath(MainService svc){
        return getLogFilePath(svc.getServiceName(), svc.getServiceContext());
    }
    
    BasicFileHandler createNewHandler( String name ){
        BasicFileHandler handler = new BasicFileHandler();
        mOcManager.populateObject(mConfig, handler);
        handler.setLogFilePrefix(name);
        handler.setLogPath(getLogPath());
        return handler;
    }
    
    BasicFileHandler getLogHandler( String name ){
        BasicFileHandler handler = null;
        if(!mAppend){
            handler = createNewHandler(name);
        }else{
            handler = (BasicFileHandler)mHandlerMap.get(name);
            if(handler==null){
                handler = createNewHandler(name);
                mHandlerMap.put(name, handler);
            }
        }
        return handler;
    }

    public void onContextCreate( RuntimeContext context ){
        MainService svc = MainService.getServiceInstance(context.getObjectContext());
        if( !svc.isTraceOn()) return;
        ConfigurableLoggerProvider provider = new ConfigurableLoggerProvider(getTopicManager());
        //provider.setLogPath(getLogPath());
        String file_name = getLogFilePath(svc);
        
        BasicFileHandler handler = getLogHandler(file_name);
        //handler.setAppend(false);
        //mOcManager.populateObject(mConfig,handler);
        provider.addHandles( new Handler[]{handler});        
        context.setInstanceOfType(BasicFileHandler.class, handler);
        
        ILoggerProvider lp = (ILoggerProvider)context.getInstanceOfType(ILoggerProvider.class);
        if(lp==null){
            //System.out.println("Directly setting instance ILoggerProviderGroup"+provider);
            context.setInstanceOfType(ILoggerProvider.class, provider);
        }else{
            if( lp instanceof ILoggerProviderGroup){
                ((ILoggerProviderGroup)lp).addLoggerProvider(provider);
                //System.out.println("Adding to group "+lp);
            }
            else{
                LoggerProviderGroup group = new LoggerProviderGroup();
                group.addLoggerProvider(provider);
                group.addLoggerProvider(lp);
                context.setInstanceOfType(ILoggerProvider.class, group );
                //System.out.println("Creating logger group "+group+" containing existing:"+lp+", service:"+provider);
            }
        }
        
        context.put(SERVICE_LOGGING_FILE, handler.getCurrentLogFile().getPath());
    }
    
    public void onContextDestroy( RuntimeContext context ){
        BasicFileHandler handler = (BasicFileHandler)context.getInstanceOfType(BasicFileHandler.class);
        if(handler!=null){
            handler.flush();
            if(!handler.getAppend())
                handler.close();
            //System.out.println("end:append set to "+handler.getAppend());
        }
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return mPattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.mPattern = pattern;
    }
    
    public void beginConfigure(CompositeMap config){
        mConfig = config;
    }
    
    /**
     * This method is called after this instance has been populated from container
     */
    public void endConfigure(){
        
    }
    
    public void onShutdown(){
        Iterator it = mHandlerMap.values().iterator();
        while(it.hasNext()){
           BasicFileHandler handler = (BasicFileHandler)it.next();
           handler.close();
        }
    }

    /**
     * @return the append
     */
    public boolean getAppend() {
        return mAppend;
    }

    /**
     * @param append the append to set
     */
    public void setAppend(boolean append) {
        mAppend = append;
    }
    
}

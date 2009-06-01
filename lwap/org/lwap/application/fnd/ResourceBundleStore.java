/*
 * Created on 2009-6-1
 */
package org.lwap.application.fnd;

import java.util.Locale;
import java.util.ResourceBundle;

import org.lwap.application.ResourceBundleFactory;
import org.lwap.application.WebApplication;

import uncertain.core.IGlobalInstance;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.database.service.DatabaseServiceFactory;

public class ResourceBundleStore implements ResourceBundleFactory, IGlobalInstance {

    IObjectRegistry         mRegistry;
    ILogger                 mLogger;   
    DatabaseServiceFactory  mSvcFactory;
    
    /**
     * @param registry
     */
    public ResourceBundleStore(IObjectRegistry registry, DatabaseServiceFactory  factory) {
        mRegistry = registry;
        mSvcFactory = factory;
    }



    public void onInitialize(){
        // ªÒ»°logger
        mLogger = LoggingContext.getLogger(WebApplication.LWAP_APPLICATION_LOGGING_TOPIC, mRegistry);
        mLogger.info("Loading prompt");
    }
    
    public ResourceBundle getResourceBundle(Locale locale) {
        
        return null;
    }

}

/*
 * Created on 2008-7-31
 */
package org.lwap.application;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.lwap.database.TransactionFactory;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.LoggingUtil;

public class EngineInitiator {

    String mHomeDir;

    String mConfigPath;

    UncertainEngine uncertainEngine;

    WebApplication application;
    
    /**
     * @param homeDir
     *            Home path to store config file
     */
    public EngineInitiator(String homeDir) {
        mHomeDir = homeDir;
    }

    public void initUncertain() throws Exception {
        String config_path = "/WEB-INF/uncertain.xml";
        File   config_file = new File( mHomeDir, "WEB-INF");
        String config_file_path = config_file.getPath();
        String config_file_name = "uncertain.xml";
        String pattern = ".*\\.config";

        uncertainEngine = new UncertainEngine(new File(config_file_path), config_file_name);
        
        IObjectRegistry os = uncertainEngine.getObjectSpace();
        os.registerInstance(application);
        CompositeLoader loader = uncertainEngine.getCompositeLoader();
        CompositeMap default_config = loader
                .loadFromClassPath("org.lwap.application.DefaultClassRegistry");
        ClassRegistry reg = (ClassRegistry) uncertainEngine.getOcManager()
                .createObject(default_config);
        uncertainEngine.addClassRegistry(reg, false);
        if (application.data_source != null) {
            os.registerInstance(DataSource.class, application.data_source);
            os.registerInstanceOnce(TransactionFactory.class,
                    application.transaction_factory);
        }
        LoggingUtil.setHandleLevels(uncertainEngine.getLogger().getParent(),
                Level.INFO);
        uncertainEngine.scanConfigFiles(pattern);
    }

    public void init() throws Exception {
        String app_path = mHomeDir;
        String config_file = "WEB-INF/application.xml";

        application = new WebApplication(app_path, config_file, null);
        CompositeMap application_conf = application.getApplicationConfig();
        initUncertain();
        application.setUncertainEngine(uncertainEngine);
        if (application.getResourceBundleFactory() == null) {
            DefaultResourceBundleFactory fact = new DefaultResourceBundleFactory(
                    "prompt");
            try {
                fact.getResourceBundle(Locale.getDefault());
                application.setResourceBundleFactory(fact);
            } catch (Exception ex) {
                System.out.println("[init] " + ex.getMessage());
            }
        }
        uncertainEngine.scanConfigFiles(".*\\.xfg");

    }

    /**
     * @return the mHomeDir
     */
    public String getHomeDir() {
        return mHomeDir;
    }

    /**
     * @param homeDir the mHomeDir to set
     */
    public void setHomeDir(String homeDir) {
        mHomeDir = homeDir;
    }

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    /**
     * @param uncertainEngine the uncertainEngine to set
     */
    public void setUncertainEngine(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;
    }
    
    public WebApplication getWebApplication(){
        return application;
    }
    
    public void shutdown(){
        application.shutdown();
        application = null;
    }
    
    
    public static void main(String[] args) throws Exception {
        String home = "G:\\Work\\Tomcat4\\webapps\\train";
        EngineInitiator ei = new EngineInitiator(home);
        ei.init();
        UncertainEngine engine = ei.getUncertainEngine();        
        //engine.scanConfigFiles(".*\\.xfg");
        try{
            while(engine.isRunning()){
                Thread.sleep(1000);
            }
        }finally{
            if(engine!=null)
                engine.shutdown();
        }
        //System.out.println("DataSource:"+ei.getUncertainEngine().getObjectSpace().getParameterOfType(javax.sql.DataSource.class));
    }    

}

/*
 * Created on 2009-4-7
 */
package uncertain.testcase.logging;

import java.util.Collection;
import java.util.logging.Level;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.core.UncertainEngine;
import uncertain.logging.ConfigurableLoggerProvider;
import uncertain.logging.ILogger;
import uncertain.logging.LoggerList;
import uncertain.logging.LoggingConfig;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;

public class ConfigurableLoggerProviderTest extends TestCase {
    
    OCManager                   ocManager;
    CompositeMapParser          parser;
    UncertainEngine             engine;
    LoggingConfig               loggingConfig;

    /**
     * @param name
     */
    public ConfigurableLoggerProviderTest(String name) {
        super(name);        
    }


    protected void setUp() throws Exception {
        super.setUp();
        engine = new UncertainEngine();
        engine.initialize(new CompositeMap());
        ocManager = engine.getOcManager();
        CompositeMap map = engine.loadCompositeMap(LoggingConfig.LOGGING_REGISTRY_PATH);
        assertNotNull(map);
        ClassRegistry reg = (ClassRegistry)ocManager.createObject(map);        
        assertNotNull(reg);
        ocManager.getClassRegistry().addAll(reg);
        
        CompositeMap loggerConfig = engine.loadCompositeMap("uncertain.testcase.logging.logging_test");
        assertNotNull(loggerConfig);
        loggingConfig = (LoggingConfig)ocManager.createObject(loggerConfig);
        assertNotNull(loggingConfig);
    }


    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testConfiguration(){
        Collection list = loggingConfig.getLoggerProviders(); 
        assertEquals(list.size(),2); 
        Object[] items = list.toArray();
        ConfigurableLoggerProvider p1 = (ConfigurableLoggerProvider)items[0];
        ILogger logger = loggingConfig.getLogger("uncertain.core");
        assertNotNull(logger);
        assertEquals(logger.getClass(), LoggerList.class);
        logger.log(Level.WARNING, "this is a test");
    }

}

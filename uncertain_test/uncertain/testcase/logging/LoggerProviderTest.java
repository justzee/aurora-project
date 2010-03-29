/*
 * Created on 2009-4-7
 */
package uncertain.testcase.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.core.UncertainEngine;
import uncertain.logging.AbstractLoggerProvider;
import uncertain.logging.BasicStreamHandler;
import uncertain.logging.LoggerProvider;
import uncertain.logging.ILogger;
import uncertain.logging.LoggerList;
import uncertain.logging.LoggingConfig;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;

public class LoggerProviderTest extends TestCase {
    
    OCManager                   ocManager;
    CompositeMapParser          parser;
    UncertainEngine             engine;
    LoggingConfig               loggingConfig;

    /**
     * @param name
     */
    public LoggerProviderTest(String name) {
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
        AbstractLoggerProvider p1 = (AbstractLoggerProvider)items[0];
        ILogger logger = loggingConfig.getLogger("uncertain.core");
        assertNotNull(logger);
        assertEquals(logger.getClass(), LoggerList.class);
        logger.log(Level.WARNING, "this is a test");
    }
    
    public void testSimpleLogging()
        throws IOException
    {
        LoggerProvider provider = new LoggerProvider();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BasicStreamHandler handler = new BasicStreamHandler(baos);
        provider.setDefaultLogLevel("WARNING");
        provider.addHandles( new Handler[]{handler} );
        
        ILogger logger1 = provider.getLogger("some.new.topic");
        ILogger logger2 = provider.getLogger("some.new.topic");
        assertTrue(logger1==logger2);
        logger1.info("Should not appear");
        logger1.warning("Should appear");
        
        baos.flush();
        String str = baos.toString();

        assertTrue(str.indexOf("Should not appear")<0);
        assertTrue(str.indexOf("Should appear")>=0);
        assertTrue(str.indexOf("WARNING")>=0);
    }

}

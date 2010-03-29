/*
 * Created on 2009-12-4 下午01:14:19
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.IParticipantManager;
import uncertain.event.ParticipantManager;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.ocm.PackageMapping;

public class ParticipantManagerTest extends TestCase {
    
    ObjectRegistryImpl      reg;
    ParticipantManager      pManager;
    CompositeLoader         loader;
    OCManager               ocManager;
    
    GlobalBean              global_bean;

    public ParticipantManagerTest(String name) {
        super(name);

    }

    protected void setUp() throws Exception {
        super.setUp();

        reg = new ObjectRegistryImpl();
        loader = CompositeLoader.createInstanceForOCM();
        global_bean = new GlobalBean("test app");
        ocManager = OCManager.getInstance();
        String pkg_name = ParticipantManager.class.getPackage().getName();
        ocManager.getClassRegistry().addPackageMapping( new PackageMapping(pkg_name,pkg_name));

        reg.registerInstance(GlobalBean.class, global_bean);
        reg.registerInstance(IObjectRegistry.class, reg);
        reg.registerInstance(IObjectCreator.class, reg);
        ocManager.setObjectCreator(reg);

  /*
        ILogger logger = LoggerProvider.createInstance(Level.FINE, System.out).getLogger("uncertain.ocm");
        reg.analysisConstructor(logger, ParticipantManager.class);
        ParticipantManager pm = (ParticipantManager)reg.createInstance(ParticipantManager.class);
        assertNotNull(pm);
      
    
        ocManager.setEventEnable(true);
        ocManager.addListener(new LoggingListener());
  */

        String config_name = this.getClass().getName();
        CompositeMap data = loader.loadFromClassPath(config_name);
        assertNotNull(data);
        pManager = (ParticipantManager)ocManager.createObject(data);
        assertNotNull(pManager);
    
    }
    
    public void testBasicConfig()
        throws Exception
    {
        List lst_app = pManager.getParticipantList(IParticipantManager.APPLICATION_SCOPE);
        assertNotNull(lst_app);        
        assertEquals(lst_app.size(), 3);
        for(Iterator it = lst_app.iterator(); it.hasNext(); )
            assertNotNull(it.next());
        List lst_service = pManager.getParticipantList(IParticipantManager.SERVICE_SCOPE);
        assertNotNull(lst_service);
        assertEquals(lst_service.size(), 2);
    }
    
    public void testEvent()
        throws Exception
    {
        Configuration config = pManager.getParticipantsAsConfig(IParticipantManager.SERVICE_SCOPE);
        Configuration child_config = new Configuration();
        child_config.setParent(config);
        Event evt = new Event("base","ServiceInit");
        child_config.fireEvent("ServiceInit", new Object[]{ evt} );
        assertEquals(evt.getSender(), "From test app");
    }

}

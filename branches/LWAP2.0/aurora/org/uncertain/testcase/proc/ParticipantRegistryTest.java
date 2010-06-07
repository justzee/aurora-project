/*
 * Created on 2005-6-3
 */
package uncertain.testcase.proc;

import junit.framework.TestCase;
import uncertain.event.EventModel;
import uncertain.proc.*;
import java.util.*;
//import java.lang.reflect.*;

/**
 * HandleMethodManagerTest
 * @author Zhou Fan
 * 
 */
public class ParticipantRegistryTest extends TestCase {
    
    ParticipantRegistry registry;
    LinkedList list;
    static final Class[] args = {ProcedureRunner.class};
    //static final Class[] args1 = {int.class, String.class};
    static final Class[] args2 = {ProcedureRunner.class, String.class};

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        registry = new ParticipantRegistry();
        list = new LinkedList();
        
        list.add( registry.new HandleMethod(
                EventModel.PRE_EVENT, 
                ParticipantTest.class.getMethod("preActionA1",args),
                ReflectionMethodHandle.ARG_TYPE_SINGLE,
                "ActionA1")
                );
        
        list.add( registry.new HandleMethod(
                EventModel.ON_EVENT, 
                ParticipantTest.class.getMethod("onACTIONA2",null),
                ReflectionMethodHandle.ARG_TYPE_NONE,
                "ACTIONA2")
                );
        
        list.add( registry.new HandleMethod(
                EventModel.POST_EVENT, 
                ParticipantTest.class.getMethod("postActionA2",null),
                ReflectionMethodHandle.ARG_TYPE_NONE,
                "ActionA2")
                );    

        list.add( registry.new HandleMethod(
                EventModel.PRE_EVENT, 
                ParticipantTest.class.getMethod("preActionA5",null),
                ReflectionMethodHandle.ARG_TYPE_NONE,
                "ActionA5")
                ); 
        list.add( registry.new HandleMethod(
                EventModel.ON_EVENT, 
                ParticipantTest.class.getMethod("onActionA5",args2),
                ReflectionMethodHandle.ARG_TYPE_MULTIPLE,
                "ActionA5")
                );         
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for HandleMethodManagerTest.
     * @param arg0
     */
    public ParticipantRegistryTest(String arg0) {
        super(arg0);
    }

    public void testGetHandleMethods() {
        ParticipantRegistry.HandleMethod[] methods
        =registry.getHandleMethods(ParticipantTest.class);
        assertEquals(methods.length, list.size());
        //System.out.println(list);
        for(int i=0; i<methods.length; i++){
            assertTrue(list.indexOf(methods[i])>=0);            
        }
        methods = registry.getHandleMethods(ParticipantTest2.class);
        assertEquals(methods.length,8);
       
    }
    
    public void testIsParticipant(){
        assertTrue(!registry.isParticipant(ParticipantTest3.class));
        int n = registry.getEntrySize();
     /*
        assertTrue(!registry.isParticipant(ParticipantTest3.class));
        assertTrue(!registry.isParticipant(ParticipantTest3.class));
        assertTrue(!registry.isParticipant(ParticipantTest3.class));
       */
        assertEquals(registry.getEntrySize(),n);
    }

}

/*
 * Created on 2005-7-25
 */
package uncertain.testcase.ocm;

import junit.framework.TestCase;
//import java.io.*;
import uncertain.composite.*;
import uncertain.ocm.*;
//import java.util.logging.*;
import java.util.*;
//import uncertain.testcase.object.*;

/**
 * ClassRegistryTest
 * @author Zhou Fan
 * 
 */
public class ClassRegistryTest extends TestCase {
    
    OCManager		ocManager;
    CompositeMap	config;
    ClassRegistry   registry;
    
    static final String[] input_ns = {"testurl","testurl","testurl2","testurl","testurl2",null,null};
    static final String[] input_element = {"class-a","contact","class-b","ContactInfo","ContactInfo","ContactInfo",null};
    static final String[] result_class = {
            "uncertain.testcase.object.ClassA",
            "uncertain.testcase.object.ContactInfo",
            "uncertain.testcase.object.ClassB",
            "testpackage.testObject",
            "testpackage.testObject",
            "testpackage.testObject",
            null
    };

    
	public ClassRegistryTest(String arg0) {
		super(arg0);
		ocManager = OCManager.getInstance();
		ocManager.getClassRegistry().registerPackage("uncertain.ocm");		
	}

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        /*
        Logger logger = ocManager.getLogger();
        logger = Logger.getAnonymousLogger();
		logger.setLevel(Level.FINE);
		logger.info("setUp called");
        */
        if(config==null){
	        config = OCManager.getDefaultCompositeLoader().loadFromStream(getClass().getClassLoader().getResourceAsStream("uncertain/testcase/ocm/ClassRegistryTest.xml"));
	        assertNotNull(config);
	        registry = (ClassRegistry)ocManager.createObject(config);	
	        assertNotNull(registry);
        }
    }

    /*
     * Class under test for PackageMapping registerPackage(String, String)
     */
    public void testRegisterPackage() {
        for(int i=0; i<input_ns.length; i++){
            CompositeMap m = new CompositeMap("t", input_ns[i], input_element[i]);
            String cls_name = registry.getClassName(m);
            if(result_class[i]==null)
                assertNull(cls_name);
            else
                assertEquals(cls_name, result_class[i]);
        }
    }
    
    public void testFeatureAttach(){
        List lst = registry.getFeatures("uncertain.testcase.object","contact-info");
        assertNotNull(lst);
        assertEquals(lst.size(),2);
        /*
        assertTrue(Collections.binarySearch(lst, ContactInfo.class)>=0);
        assertTrue(Collections.binarySearch(lst, ContactInfo2.class)>=0);
        */
    }


}

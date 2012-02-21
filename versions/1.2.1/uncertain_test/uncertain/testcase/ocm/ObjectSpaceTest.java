/*
 * Created on 2005-6-15
 */
package uncertain.testcase.ocm;

import junit.framework.TestCase;
import uncertain.ocm.*;
import uncertain.testcase.object.*;

/**
 * ObjectSpaceTest
 * @author Zhou Fan
 * 
 */
public class ObjectSpaceTest extends TestCase {
    
    ObjectRegistryImpl root;
    ObjectRegistryImpl child;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        root = new ObjectRegistryImpl();
        child = new ObjectRegistryImpl(root);        
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for ObjectSpaceTest.
     * @param arg0
     */
    public ObjectSpaceTest(String arg0) {
        super(arg0);
    }

    public void testCreateInstance() throws Exception {
        // test for create simple instance
        root.registerInstance(String.class, "seacat.zhou@hand-china.com");
        root.registerInstance(int.class, new Integer(135014));
        ContactInfo2 c2 = (ContactInfo2)root.createInstance(ContactInfo2.class);
        assertNotNull(c2);
        assertEquals(c2.getEmail(),"seacat.zhou@hand-china.com");
        assertEquals(c2.getPhone(), 135014);
        // test for create instance from specified constructor
        Name n = new Name("Fan","Zhou");
        root.registerInstance(Name.class, n);
        Person p1 = (Person)root.createInstance(Person.class);
        assertNotNull(p1);
        assertTrue(p1.getName().equals(n));
        // test for create instance from child ObjectSpace
        child.registerInstance(ContactInfo2.class, c2);
        root.registerInstance(boolean.class, new Boolean(true));
        Person p2 = (Person)child.createInstance(Person.class);
        assertNotNull(p2);
        assertTrue(p2.getContactInfo().equals(c2));
        assertTrue(p2.Assigned);        
    }

}

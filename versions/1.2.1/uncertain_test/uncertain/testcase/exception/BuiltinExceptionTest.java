/*
 * Created on 2011-6-29 下午02:19:38
 * $Id$
 */
package uncertain.testcase.exception;

import junit.framework.TestCase;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

public class BuiltinExceptionTest extends TestCase {
    
    public void testMessage(){
        
        ConfigurationFileException ex = BuiltinExceptionFactory.createAttributeMissing(new ILocatable(){

            public Location getOriginLocation() {
                Location l = new Location();
                l.setStartPoint(10,20);
                return l;
            }

            public String getOriginSource() {
                return "/usr/local/myfile.txt";
            }
            
        }, "attrib1");
        
        assertEquals(ex.getOriginSource(),"/usr/local/myfile.txt");
        assertEquals(ex.getOriginLocation().getStartLine(),10);
        //assertEquals("Source File:/usr/local/myfile.txt, Line:10, Column:20 Attribute \"attrib1\" must be set. Please check source file and set this attribute.", ex.getMessage());
    }

}

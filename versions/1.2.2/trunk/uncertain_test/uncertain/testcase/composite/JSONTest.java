/*
 * Created on 2010-9-7 下午05:01:40
 * $Id$
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;

public class JSONTest extends TestCase {
    
    public void testNull()
        throws Exception
    {
        JSONObject j1 = new JSONObject("{\"K1\":null,\"K2\":\"value\"}");
        JSONObject j2 = new JSONObject();
        j2.put("K1", JSONObject.NULL);
        j2.put("K2", "value");
        assertEquals(j1.toString(), j2.toString());
        CompositeMap m = JSONAdaptor.toMap(j2);
        Object value = m.get("K1");
        assertNull(value);
        System.out.println(j2.toString());
        System.out.println(m.toXML());
    }

    public JSONTest(String name) {
        super(name);
    }

}

/*
 * Created on 2011-9-8 上午10:54:43
 * $Id$
 */
package uncertain.testcase.util;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class QuickParserTest extends TestCase {
    
    static final String INPUTS[] = {
        "TEXT${/session/@user_id}${/model/list/record/@id}1",
        "NO TAG",
        "${/session/@user_id}",
        "C${/session/@user_id}D",
        "C${/session/@nodata}D",
        "${/session/@user_id}D",
        "D${/session/@user_id}",
        "D${/session/@user_id}.${/model/list/record/@id}.${/session/@user_id}.${/model/list/record/@id}",
        "A",
        "",
        "A ${/session/@user_id broken tag",
        "A ${/session/@user_id}}",
        "$",
        "$('id')",
        "$$('id')",
        "$$$$$$$$$$$$$$$$",
        "$(id1)${id2}$(id3)$$(id4)$$$(id5)",
        "A${{/session/@user_id}B",
        "A${{/session/@user_id}}B",
        "$$$$${/session/@user_id}",
        "$${/session/@user_id}$${/session/@user_id}",
        "${/session/@user_id}${/session/@user_id}${/session/@user_id}${/session/@user_id}",
        "(new RegExp(\"(^| )app_theme=([^;]*)(;|$)\"))",
        "$('${id}').bind('${bindtarget}');"
    };

    static final String RESULTS[] = {
        "TEXT1000ID01",
        "NO TAG",
        "1000",
        "C1000D",
        "CD",
        "1000D",
        "D1000",
        "D1000.ID0.1000.ID0",
        "A",
        "",
        "A ${/session/@user_id broken tag",
        "A 1000}",
        "$",
        "$('id')",
        "$$('id')",
        "$$$$$$$$$$$$$$$$",
        "$(id1)$(id3)$$(id4)$$$(id5)",
        "A1000B",
        "A1000}B",
        "$$$$1000",
        "$1000$1000",
        "1000100010001000",
        "(new RegExp(\"(^| )app_theme=([^;]*)(;|$)\"))",
        "$('').bind('');"
    };
    
    CompositeMap    data;

    public QuickParserTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        data = new CompositeMap("context");
        CompositeMap model = data.createChild("model");
        CompositeMap session = data.createChild("session");
        session.put("user_id", new Long(1000));
        session.put("role_id", "2000");
        CompositeMap child = model.createChild("list");
        for( int i=0; i<20; i++){
            child.createChild("record").put("id", "ID"+i);
        }
    }
    
    public void testParse(){
       for( int i=0; i<INPUTS.length; i++){
           String intput = INPUTS[i];
           String result = RESULTS[i];
           String parsed = TextParser.parse(intput, data);
           assertEquals(result,parsed);
       }
        
    }
    
    

}

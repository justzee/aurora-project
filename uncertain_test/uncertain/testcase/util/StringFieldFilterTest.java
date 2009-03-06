/*
 * Created on 2007-12-7
 */
package uncertain.testcase.util;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.util.StringFieldFilter;

public class StringFieldFilterTest extends TestCase {
    
    CompositeMap[] records = new CompositeMap[6];
    
    boolean accepts_case[] = { false, true, true, false, false, false};
    boolean accepts_no_case[] = { false, true, true, false, true, true};

    public StringFieldFilterTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        for(int i=0; i<records.length; i++)
            records[i] = new CompositeMap("record");
        records[0].put("id", null);
        records[1].put("id", "SEARCH_STARTS");
        records[2].put("id", "IN_SEARCH_STARTS");
        records[3].put("id", "NO_MATCH");
        records[4].put("id", "search_starts");
        records[5].put("id", "in_search_starts");

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testAccepts(){
        StringFieldFilter filter1 = new StringFieldFilter("id", false, true);
        filter1.setSearchText("SEARCH");
        StringFieldFilter filter2 = new StringFieldFilter("id", false, false);
        filter2.setSearchText("SEARCH");
        for(int i=0; i<records.length; i++){
            assertEquals(filter1.accepts(records[i]), accepts_case[i]);
            assertEquals(filter2.accepts(records[i]), accepts_no_case[i]);            
        }
        StringFieldFilter filter3 = new StringFieldFilter("id", true, true);
        filter3.setSearchText("SEARCH");
        assertTrue(filter3.accepts(records[1]));
        assertTrue(!filter3.accepts(records[2]));
    }

}

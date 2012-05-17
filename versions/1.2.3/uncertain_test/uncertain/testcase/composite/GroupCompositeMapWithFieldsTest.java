/*
 * Created on 2005-11-18
 */
package uncertain.testcase.composite;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import uncertain.composite.*;
import uncertain.composite.transform.*;

/**
 * GroupCompositeMapWithFieldsTest
 * @author linjinxiao
 * 
 */
public class GroupCompositeMapWithFieldsTest extends TestCase {

    CompositeMap target ;
	GroupCompositeMapWithFields instance;
	
    public GroupCompositeMapWithFieldsTest(String arg0) {
        super(arg0);
    }
	/*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
		target = new CompositeMap();
		String[][] rows = new String[][] {
				{ "a1", "a2", "a3", "a4", "a5", "a6" },
				{ "a1", "a2", "a4", "a5", "a7", "a9" },
				{ "a2", "a3", "a4", "a9", "a2", "a6" },
				{ "a3", "a2", "a4", "a6", "a3", "a7" },
				{ "a3", "a8", "a1", "a4", "a4", "a8" },
				{ "a3", "a7", "a2", "a3", "a5", "a9" } };
		String[] allFields = new String[] { "B1", "B2", "B3", "B4", "B5", "B6" };
		for (int x = 0; x < rows.length; x++) {
			CompositeMap child = new CompositeMap();
			for (int y = 0; y < rows[x].length; y++) {
				child.put(allFields[y], rows[x][y]);
			}
			target.addChild(child);
		}
		instance = GroupCompositeMapWithFields.getInstance();
		
    }

    public void testGroupCompositeMap(){
//		System.out.println(target.toXML());
    	String[] groupfields = new String[] { "B1", "B2", "B3" };
		List mergeRanges = instance.groupCompositeMap(target, groupfields);
		
		int[] testMergeRanges = new int[]{3,0,5,0}; 
		
		Iterator it = mergeRanges.iterator();
		boolean exists = false;
		while (it.hasNext()) {
			int[] range = (int[]) it.next();
			boolean same = true;
			for (int i = 0; i < range.length; i++) {
				if(range[i] != testMergeRanges[i]){
					same = false;
					break;
				}
			}
			if(same){
				exists = true;
				break;
			}
		}
		assertTrue(exists);
    }

}

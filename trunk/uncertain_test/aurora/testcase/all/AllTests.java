/*
 * Created on 2011-4-20 上午10:32:16
 * $Id$
 */
package aurora.testcase.all;

import uncertain.testcase.core.UncertainEngineTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        // suite.addTestSuite();
        //$JUnit-END$
        return suite;
    }

}

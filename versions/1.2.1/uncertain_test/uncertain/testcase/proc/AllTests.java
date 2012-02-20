/*
 * Created on 2010-6-3 下午05:22:04
 * $Id$
 */
package uncertain.testcase.proc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for uncertain.testcase.proc");
        //$JUnit-BEGIN$
        suite.addTestSuite(ParticipantRegistryTest.class);
        suite.addTestSuite(ExceptionHandleTest.class);
        suite.addTestSuite(SwitchTest.class);
        suite.addTestSuite(ProcedureRegistryTest.class);
        suite.addTestSuite(ContextFieldTest.class);
        suite.addTestSuite(AssertTest.class);
        suite.addTestSuite(ProcedureRunnerTest.class);
        suite.addTestSuite(FieldTest.class);
        //$JUnit-END$
        return suite;
    }

}

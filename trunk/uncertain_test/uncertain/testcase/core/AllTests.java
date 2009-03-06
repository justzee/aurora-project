/*
 * Created on 2006-11-23
 */
package uncertain.testcase.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import uncertain.testcase.ocm.*;
import uncertain.testcase.proc.*;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for uncertain.testcase.core");
        //$JUnit-BEGIN$
        suite.addTestSuite(UncertainEngineTest.class);
        //uncertain.ocm
        suite.addTestSuite(ClassAnalyzerTest.class);
        suite.addTestSuite(ClassRegistryTest.class);
        suite.addTestSuite(NamingUtilTest.class);
        suite.addTestSuite(ObjectSpaceTest.class);
        suite.addTestSuite(OCManagerTest.class);
        //uncertain.proc
        suite.addTestSuite(ParticipantRegistryTest.class);
        suite.addTestSuite(ProcedureRunnerTest.class);
        suite.addTestSuite(SwitchTest.class);
        suite.addTestSuite(AssertTest.class);
        suite.addTestSuite(ContextFieldTest.class);
        //$JUnit-END$
        return suite;
    }

}

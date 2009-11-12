/*
 * Created on 2006-11-23
 */
package uncertain.testcase.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import uncertain.testcase.composite.XMLOutputterTest;
import uncertain.testcase.ocm.ClassAnalyzerTest;
import uncertain.testcase.ocm.ClassRegistryTest;
import uncertain.testcase.ocm.NamingUtilTest;
import uncertain.testcase.ocm.OCManagerTest;
import uncertain.testcase.ocm.ObjectSpaceTest;
import uncertain.testcase.proc.AssertTest;
import uncertain.testcase.proc.ContextFieldTest;
import uncertain.testcase.proc.ParticipantRegistryTest;
import uncertain.testcase.proc.ProcedureRunnerTest;
import uncertain.testcase.proc.SwitchTest;


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
        suite.addTestSuite(XMLOutputterTest.class);
        //$JUnit-END$
        return suite;
    }

}

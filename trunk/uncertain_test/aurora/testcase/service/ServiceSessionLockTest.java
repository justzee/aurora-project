/*
 * Created on 2011-5-23 下午11:24:30
 * $Id$
 */
package aurora.testcase.service;

import junit.framework.TestCase;
import aurora.service.lock.ServiceSessionLock;

public class ServiceSessionLockTest extends TestCase {
    
    ServiceSessionLock      mLock;

    public ServiceSessionLockTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        mLock = new ServiceSessionLock();
    }

    protected void tearDown() throws Exception {
        mLock.shutdown();
    }
 
    public void testLock()
        throws Exception
    {

        for(int i=0; i<100; i++)
            mLock.lock(Integer.toString(i), "TestService", 0);
        for(int i=0; i<100; i++)
            assertTrue(mLock.islocked(Integer.toString(i), "TestService"));
    
        for(int i=0; i<100; i++)
            mLock.unlock(Integer.toString(i), "TestService");
        for(int i=0; i<100; i++)
            assertTrue(!mLock.islocked(Integer.toString(i), "TestService"));
        
        mLock.lock("1", "TestService", 100);
        //System.out.println(mLock.dumpLocks());
        Thread.sleep(101);
        assertTrue(!mLock.islocked("1","TestService"));
    
    }
    

}

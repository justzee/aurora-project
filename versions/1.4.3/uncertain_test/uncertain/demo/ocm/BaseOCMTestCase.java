/*
 * Created on 2009-7-18
 */
package uncertain.demo.ocm;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;

public class BaseOCMTestCase extends TestCase {
    
    protected OCManager         mOcManager;
    //protected DocumentFactory   mDocFactory;
    protected CompositeLoader   mCompositeLoader;

    public BaseOCMTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mOcManager = OCManager.getInstance();
        mCompositeLoader = new CompositeLoader();
    }
    
    public OCManager getOCManager(){
        return mOcManager;
    }
    
    public CompositeMap loadDocument( String name )
        throws IOException, SAXException
    {
        return mCompositeLoader.loadFromClassPath(name);
    }

}

/*
 * Created on 2009-7-18
 */
package uncertain.demo.ocm;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.document.DocumentFactory;
import uncertain.ocm.OCManager;

public class BaseOCMTestCase extends TestCase {
    
    protected OCManager         mOcManager;
    protected DocumentFactory   mDocFactory;

    public BaseOCMTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mOcManager = OCManager.getInstance();
        mDocFactory = new DocumentFactory();
    }
    
    public OCManager getOCManager(){
        return mOcManager;
    }
    
    public CompositeMap loadDocument( String name )
        throws IOException, SAXException
    {
        return mDocFactory.loadCompositeMap(name);
    }

}

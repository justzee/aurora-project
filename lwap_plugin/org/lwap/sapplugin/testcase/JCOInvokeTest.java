/*
 * Created on 2006-11-14
 */
package org.lwap.sapplugin.testcase;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.lwap.sapplugin.JcoInvoke;
import org.lwap.sapplugin.Parameter;
import org.lwap.sapplugin.SapInstance;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;
import uncertain.proc.ProcedureRunner;

public class JCOInvokeTest extends TestCase {
    
    SapInstance inst;
    Logger logger;
    JcoInvoke   jcoInvoke;
    
  
    public JCOInvokeTest(String name){
        super(name);
        inst = new SapInstance();
        logger = Logger.getLogger("JCOInvokeTest");
    }

    protected void setUp() throws Exception {
        inst.DEFAULT_LANG = "EN";
        inst.MAX_CONN = 20;
        inst.PASSWORD = "amechand";
        inst.SAP_CLIENT = "800";
        inst.SERVER_IP = "192.168.0.252";
        inst.SID = "PRD";
        inst.USERID = "sapadm";
    }
/*
    public void testA(){
        Object o = null;
        assertNotNull(o);
    }
*/    
    public void testJcoInvoke() throws Exception {
        //try{
            jcoInvoke = new JcoInvoke(inst, logger);
            jcoInvoke.Function = "ZCONVERT_TO_LOCAL_CURRENCY";
            jcoInvoke.Return_target = "result";
            jcoInvoke.Parameters = new Parameter[]{
                    new Parameter("DATE","20061101",null),
                    new Parameter("FOREIGN_AMOUNT","100",null),
                    new Parameter("FOREIGN_CURRENCY","USD",null),
                    new Parameter("LOCAL_CURRENCY","CNY",null),
                    new Parameter("TYPE_OF_RATE","M",null),
                    new Parameter("LOCAL_AMOUNT",null,"@VALUE")
                    //,new Parameter("EXCHANGE_RATE","@UC_RATE",null)
                    
            };
            ProcedureRunner r = new ProcedureRunner();
            jcoInvoke.run(r);
            Object result = r.getContext().getObject("/model/result/@VALUE");
            assertNotNull(result);
            assertEquals(result.toString(),"787.20");
            
        /*
    } catch(Throwable ex){
            logger.severe(ex.getMessage());
        }
        */
        //System.out.println(r.getContext().toXML());
    }
    
    public static void main(String[] args) throws Exception {
        OCManager om = new OCManager();
        om.getClassRegistry().addPackageMapping( new PackageMapping("org.lwap.sapplugin","org.lwap.sapplugin") );
        InputStream is = JCOInvokeTest.class.getClassLoader().getResourceAsStream("org/lwap/sapplugin/testcase/jco_sample.xml");
        CompositeMap map = CompositeMapParser.parse(is);
        JcoInvoke ji = new JcoInvoke(null,null);
        om.populateObject(map, ji);
        System.out.println(ji.toString());
    }
    

}

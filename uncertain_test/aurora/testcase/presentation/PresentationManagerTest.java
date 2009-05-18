/*
 * Created on 2009-5-2
 */
package aurora.testcase.presentation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.logging.ConfigurableLoggerProvider;
import uncertain.logging.DefaultLogger;
import uncertain.ocm.ClassRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.presentation.TemplateBasedView;
import aurora.presentation.ViewComponent;
import aurora.presentation.ViewComponentPackage;
import aurora.presentation.component.HtmlPage;
import aurora.presentation.component.TemplateRenderer;

public class PresentationManagerTest extends TestCase {
    
    public static final String AURORA_TESTCASE_UI = "aurora.testcase.ui";
    //PackageManager       mPkgManager;
    File                 mBasePath;
    UncertainEngine      mEngine;
    PresentationManager  mPrManager;
    ViewComponentPackage mPackage;    
    
    static String[] required_content = {
        "<input type=\"text\" name=\"name\"", 
        
        "<select name=\"deptid\"",
        "<option value=\"0\" >All",
        "<option value=\"20\" selected >Development",
        "class=\"ui.input.textarea\"",
        "<script src='resource/aurora.testcase.ui/default/textedit.js'>",
        "Name: <input type=\"text\" name=\"name\"",
        "onclick=\"alert('test L.A.');\""
        };

    public PresentationManagerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        URL url = Thread.currentThread().getContextClassLoader().getResource("aurora/testcase/ui");
        mBasePath = new File(url.getFile());
        mEngine = new UncertainEngine();
        mEngine.initialize( new CompositeMap());
        mPrManager = new PresentationManager(mEngine);
        //mPkgManager = mPrManager.getPackageManager();
        mPackage = mPrManager.loadViewComponentPackage(mBasePath.getPath());        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testLoadPackage()
        throws Exception
    {
 
        assertNotNull(mPackage);
        assertEquals(mPackage.getName(), AURORA_TESTCASE_UI);
        ViewComponentPackage pkg1 = mPrManager.getPackage(AURORA_TESTCASE_UI);
        assertTrue(mPackage==pkg1);
        
        ClassRegistry reg = mPackage.getClassRegistry();
        assertNotNull(reg);
        CompositeMap textedit = new CompositeMap("textedit");
        List cls = reg.getFeatures(textedit);
        assertNotNull(cls);
        
        ViewComponent c1 = mPackage.getComponent(textedit);
        assertEquals(c1.getBuilder(), TemplateBasedView.class);
        
        ViewComponent c2 = mPackage.getComponent( new CompositeMap("html-page"));
        assertEquals(c2.getBuilder(), HtmlPage.class);
        
        ViewComponent c3 = mPackage.getComponent( new CompositeMap("template"));
        assertEquals(c3.getBuilder(), TemplateRenderer.class);
        
        ViewComponent c4 = mPackage.getComponent( new CompositeMap("select") );
        assertEquals(c4.getBuilder(), TemplateBasedView.class);
        
    }
    
    public void testGetResource()
        throws Exception
    {
        File js1 = mPackage.getResourceFile("textedit.js");
        assertNotNull(js1);
        assertTrue(js1.exists());
        
        assertTrue(mPackage.isResourceExist("warmlook", "textedit.js"));
        assertTrue( !mPackage.isResourceExist("warmlook", "css/only_in_default.css"));
        File js2 = mPackage.getResourceFile("warmlook", "textedit.js");
        assertNotNull(js2);
        assertTrue(js2.exists());
        
        FileReader reader = new FileReader(js2);
        BufferedReader br = new BufferedReader(reader);
        String line1 = br.readLine();
        assertEquals(line1, "//js for warmlook");
        
        File css1 = mPackage.getResourceFile("css/only_in_default.css");
        assertNotNull(css1);
        assertTrue(css1.exists());
        //System.out.println(css1.getPath());
        
        File css2 = mPackage.getResourceFile("warmlook", "css/only_in_default.css");
        assertNotNull(css2);
        assertEquals(css2.getPath(), css1.getPath());
        
    }
    
    static CompositeMap createOption(String value, String prompt ){
        CompositeMap record = new CompositeMap("record");
        record.put("name", prompt);
        record.put("value", value);
        return record;
    }
    
    public void testBuildView() throws Exception {
        
        CompositeMap model = new CompositeMap("record");
        model.put("name", "Johnson");
        model.put("address", "L.A.");
        model.put("deptid", "20");
        
        CompositeMap depts = model.createChild("depts");
        depts.addChild( createOption("10","Sales") );
        depts.addChild( createOption("20","Development") );
        depts.addChild( createOption("30","Finance") );

        CompositeMap form = new CompositeMap("html-page");
        form.put("name","employee_form");
        form.put("package", AURORA_TESTCASE_UI);
        
        CompositeMap name = new CompositeMap("textedit");
        name.put("datafield", "@name");
        name.put("name", "name");
        form.addChild(name);
        
        CompositeMap address = new CompositeMap("textedit");
        address.put("datafield", "@address");
        address.put("value", "address is '${@address}'");
        address.put("name", "address");
        address.put("class", "textedit.normal");
        address.put("enabled", "false");
        address.put("onclick", "alert('test ${@address}');");
        address.put("onblur", "alert('leave');");
        form.addChild(address);
     
        CompositeMap memo = new CompositeMap("textarea");
        memo.put("name", "memo");
        memo.put("rows", "7");
        form.addChild(memo);

        CompositeMap select = new CompositeMap("select");
        select.put("datasource", "depts");
        select.put("displayfield", "@name");
        select.put("valuefield", "@value");
        select.put("name", "deptid");
        select.put("datafield", "@deptid");
        CompositeMap o = select.createChildByTag("options/record");
        o.put("value", "0");
        o.put("prompt", "All");
        form.addChild(select);
        
        ConfigurableLoggerProvider pr = ConfigurableLoggerProvider.createInstance("aurora.presentation", Level.INFO);
        pr.getTopicManager().setTopicLevel(Configuration.LOGGING_TOPIC, Level.INFO);
        DefaultLogger logger = (DefaultLogger)pr.getLogger(Configuration.LOGGING_TOPIC); 

        ByteArrayOutputStream  bos = new ByteArrayOutputStream(20000);
        PrintWriter out = new PrintWriter(bos);
        BuildSession session = mPrManager.createSession(out);
        session.setLoggerProvider(pr);
        assertNull( session.getResourceUrl(AURORA_TESTCASE_UI, "no_file.gif"));
        session.setTheme("warmlook");
        assertNotNull( session.getResourceUrl(AURORA_TESTCASE_UI, "css/only_in_default.css"));
        session.setTheme("default");
        
        /*
        boolean b = session.includeResource(AURORA_TESTCASE_UI,"textedit.js");
        assertTrue(!b);
        b = session.includeResource(AURORA_TESTCASE_UI,"textedit.js");
        assertTrue(b);
        */
        session.buildView(model, form);
        session.getWriter().flush();
        String content = bos.toString();
        System.out.println(content);        
        for(int i=0; i<required_content.length; i++){
            int index = content.indexOf(required_content[i]);
            try{
                assertTrue(index>=0); 
            }catch(Error ex){
                System.out.println(required_content[i]+" doesn't exit");
                throw ex;
            }
        }
            

    }
        

}

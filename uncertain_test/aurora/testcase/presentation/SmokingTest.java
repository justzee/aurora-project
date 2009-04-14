/*
 * Created on 2007-8-15 ÏÂÎç10:49:38
 */
package aurora.testcase.presentation;

import java.io.PrintWriter;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.ocm.FeatureAttach;
import uncertain.ocm.OCManager;
import uncertain.proc.ParticipantRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.util.template.ResourceTemplateFactory;

public class SmokingTest extends TestCase {
    
    PresentationManager pm;
    OCManager           ocManager;
    ParticipantRegistry reg;
    ResourceTemplateFactory factory;
    
    void setUpReg(OCManager ocManager, String name) throws Exception {
        
        FeatureAttach f2 = new FeatureAttach(null, name, "aurora.presentation.features.FieldBinding");
        ocManager.getClassRegistry().addFeatureAttach(f2);        

        FeatureAttach f3 = new FeatureAttach(null, name, "aurora.presentation.features.EventAware");
        ocManager.getClassRegistry().addFeatureAttach(f3);
        
        FeatureAttach f4 = new FeatureAttach(null, name, "aurora.presentation.component.InputComponent");
        ocManager.getClassRegistry().addFeatureAttach(f4);
        
    }

    
    
    public void setUp() throws Exception {
        factory = new ResourceTemplateFactory("aurora/resource/ui/");
        ocManager = OCManager.getInstance();
        reg = new ParticipantRegistry();
        pm = new PresentationManager(ocManager);
        pm.setTemplateFactory(factory);
        
        setUpReg(ocManager, "textedit");
        FeatureAttach f1 = new FeatureAttach(null, "textedit", "aurora.presentation.component.TextEditRenderer");
        ocManager.getClassRegistry().addFeatureAttach(f1);
        
        setUpReg(ocManager, "textarea");
        f1 = new FeatureAttach(null, "textarea", "aurora.presentation.component.TextAreaRenderer");
        ocManager.getClassRegistry().addFeatureAttach(f1);
        
        setUpReg(ocManager, "select");
        f1 = new FeatureAttach(null, "select", "aurora.presentation.component.SelectRenderer");
        ocManager.getClassRegistry().addFeatureAttach(f1);
        f1 = new FeatureAttach(null, "select", "aurora.presentation.features.OptionSelectOne");
        ocManager.getClassRegistry().addFeatureAttach(f1);
    }
    
    public   SmokingTest(String name){
        super(name);
    }
    
    static CompositeMap createOption(String value, String prompt ){
        CompositeMap record = new CompositeMap("record");
        record.put("name", prompt);
        record.put("value", value);
        return record;
    }
    
    public void testBuildView() throws Exception {
        BuildSession session = pm.createSession(new PrintWriter(System.out));
        CompositeMap model = new CompositeMap("record");
        model.put("name", "Johnson");
        model.put("address", "L.A.");
        model.put("deptid", "20");
        
        CompositeMap depts = model.createChild("depts");
        depts.addChild( createOption("10","Sales") );
        depts.addChild( createOption("20","Development") );
        depts.addChild( createOption("30","Finance") );
        
        CompositeMap view = new CompositeMap("textedit");
        view.put("datafield", "@address");
        view.put("value", "address is '${@address}'");
        view.put("name", "address");
        view.put("class", "textedit.normal");
        view.put("enabled", "false");
        view.put("onclick", "alert('test ${@address}');");
        view.put("onblur", "alert('leave');");
        
        session.buildView(model, view);
        session.getWriter().flush();
        System.out.println();
        
        view.setName("textarea");
        view.put("rows", "7");
        session = pm.createSession(new PrintWriter(System.out));
        session.buildView(model, view);
        session.getWriter().flush();
        System.out.println();

        CompositeMap select = new CompositeMap("select");
        select.put("datasource", "depts");
        select.put("displayfield", "@name");
        select.put("valuefield", "@value");
        select.put("name", "deptid");
        select.put("datafield", "@deptid");
        CompositeMap o = select.createChildByTag("options/record");
        o.put("value", "0");
        o.put("prompt", "All");
        
        session.buildView(model, select);
        session.getWriter().flush();
    
    }

    
}

/*
 * Created on 2014-8-28 下午10:46:28
 * $Id$
 */
package aurora.bpm.model;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;

public class WorkflowFactory {
    
    public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    
    OCManager           ocManager;
    CompositeLoader     loader;
    
    public WorkflowFactory(){
        ocManager = new OCManager();
        loader = new CompositeLoader(".", "bpmn");
        loader.setCaseInsensitive(true);
        ClassRegistry reg = ocManager.getClassRegistry();
        PackageMapping pkm = new PackageMapping(BPMN_NAMESPACE, this.getClass().getPackage().getName());
        reg.addPackageMapping( pkm);
    }
    
    public Definitions loadFromClassPath(String cp) throws IOException, SAXException {
        CompositeMap map = loader.loadFromClassPath(cp);
        if(map==null)
            throw new IOException("Can't load "+cp);
        Definitions df = (Definitions)ocManager.createObject(map);
        df.validate();
        /*
        if(df!=null){
            Process p = df.getProcess();
            if(p!=null) p.resolveReference();
        }
        */
        return df;
    }
    
    public static void main(String[] args) throws Exception {
        WorkflowFactory fact = new WorkflowFactory();
        //System.out.println(fact.ocManager.getReflectionMapper().getMappingRule(SequenceFlow.class));
        Definitions def = fact.loadFromClassPath("aurora.bpm.testcase.MyProcess");
        Process process = def.getProcess();
        ProcessInstance instance = new ProcessInstance(process);
        instance.start();
        System.out.println("Finished!");
    }

}

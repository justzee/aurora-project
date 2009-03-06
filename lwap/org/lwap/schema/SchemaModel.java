/*
 * Created on 2005-10-12
 */
package org.lwap.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.proc.ProcedureRunner;
/**
 * SchemaModel
 * @author Zhou Fan
 * 
 */
public class SchemaModel {
    
    SchemaFactory fact;

    /**
     * 
     */
    public SchemaModel(SchemaFactory fact) {
        this.fact = fact;
    }
    
    public void onCreateModel(ProcedureRunner runner) throws IOException, SAXException  {
        CompositeMap context = runner.getContext();  
        Object o = context.getObject("parameter/@element");
        Object prefix = context.getObject("parameter/@prefix");
        if(o==null) throw new IllegalArgumentException("element is requried");
        String file_name = o.toString()+".xsd";   
        if (prefix!=null){     	
        	file_name=prefix.toString()+"."+file_name;
        }        	       
        File f = new File(fact.getBaseDir(), file_name);        
        FileInputStream fis = null;
        CompositeMap    m = null; 
 
        CompositeMap attribs = new CompositeMap("attrib-list");
        CompositeMap refElements = new CompositeMap("element-list");
        try{
            fis = new FileInputStream(f);
            m = CompositeMapParser.parse(fis); 
            m = m.getChild("complexType");
            if(m!=null){
	            Iterator it = m.getChildIterator();
	            if(it!=null)
   	  	            	
	            while(it.hasNext())
	            {
	                CompositeMap child = (CompositeMap)it.next(); 
	                if("attribute".equals(child.getName()))
	                    attribs.addChild(child);
   	                
	                if("all".equals(child.getName())) {
	                	CompositeMap m1 = m.getChild("all");
		                if(m1!=null){
		    	            Iterator it1 = m1.getChildIterator();
		    	            if(it1!=null)
		    	            while(it1.hasNext())
		    	            {
		    	                CompositeMap child1 = (CompositeMap)it1.next(); 
		    	                if("element".equals(child1.getName()))
		    	                	refElements.addChild(child1); 
		    	            }   
		                }
	                }
	            }
            }else{
      //          System.out.println("no complexType");
            }
        }finally{
            if(fis!=null) fis.close();
        } 
       // CompositeMap model = context.createChild("model");
        CompositeMap model = context.getChild("model");
        if (model==null) 
        {
        	model = context.createChild("model");
        }
    	          
        int attribCount=0;
        int refElementCount=0;
        if (attribs.getChilds()!=null) 
        {
        	attribCount=attribs.getChilds().size() ;
        }   
        if (refElements.getChilds()!=null) 
        {
        	refElementCount=refElements.getChilds().size() ;
        }   
        CompositeMap cc=new CompositeMap("elementcount");
        cc.putInt("AttribCount",attribCount );
        cc.putInt("RefElementCount",refElementCount ); 
        model.addChild(cc);
        model.addChild(attribs);
        model.addChild(refElements);  
//System.out.println("cc is "+cc.toXML());        
/*
        NameSpace[] NameSpaceRegistry = fact.NameSpaceRegistry; 
        CompositeMap m = new CompositeMap("ns-list");
        for(int i=0; i<NameSpaceRegistry.length; i++){
            CompositeMap item = new CompositeMap("item");
            item.put("ns", NameSpaceRegistry[i].toString());
            m.addChild(item);
        }
        
        CompositeMap context = runner.getContext();
        MainService service = (MainService)context.get(MainService.KEY_SERVICE_INSTANCE);
        CompositeMap model = service.getModel();
        model.addChild(m);
*/        
        
    }
/*    
    public void postCreateView(ProcedureRunner runner){
        CompositeMap context = runner.getContext();
        MainService service = (MainService)context.get(MainService.KEY_SERVICE_INSTANCE);
        
        CompositeMap view = service.getView();
        CompositeMap h = new CompositeMap("input");
        h.put("type","text");
        h.put("size","30");
        view.addChild(h);
    }
*/
}

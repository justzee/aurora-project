/*
 * Created on 2005-10-11
 */
package org.lwap.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
   
/**
 * Element
 * @author Zhou Fan
 * 
 */
public class Element extends DynamicObject {
 
	public String getElementName() {
        return getString(SchemaFactory.KEY_NAME);
    }
    
    public void setElementName(String name){
        putString(SchemaFactory.KEY_NAME, name);
    }
    
    public String getCategoryName() {
    	return  getString(SchemaFactory.CATEGORY_NAME);  
    }
    
    public void  setCategoryName(String categoryName) {
    	putString(SchemaFactory.CATEGORY_NAME, categoryName);
    }
    
    public String getElementNS(){
        return getObjectContext().getNamespaceURI() ;
    }
    
    public List getAttributeList(){ 
        CompositeMap m 	  =new CompositeMap();
        List 		 list =new ArrayList();
        m = getObjectContext().getChild("complexType");
        
        if(m!=null){
	       Iterator it = m.getChildIterator();
	       if(it!=null)
	       while(it.hasNext())
	       {
	          CompositeMap child = (CompositeMap)it.next(); 
	          if("attribute".equals(child.getName()))
	               list.add(it);
	       }
        } 
        return list;
    }
    
    public void addAttribute(Attribute attrib){
    	CompositeMap m 		 =new CompositeMap();
        m = getObjectContext().getChild("complexType");         
        if(m!=null){
	       m.addChild(attrib.getObjectContext());	        
        } 
    }

    public void addAttribute(String attrib_name)  {
    	CompositeMap m 		 = new CompositeMap("attribute"); 
        Attribute attrib 	 = null;
     	m.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
     	m.putString(SchemaFactory.KEY_NAME,attrib_name);
     	m.putString("type","xs:string");
     	m.putString("use", "optional");
     	try {
			attrib=(Attribute)DynamicObject.cast(m, Attribute.class);
		} catch (Exception e) {   
		} 	 	
		addAttribute(attrib); 
    }
    
    public Attribute getAttribute(String attrib_name)  {
    	CompositeMap m 		 = getObjectContext().getChild("complexType");  
    	Iterator it = m.getChildIterator();
    	Attribute attrib = null;
    	if (it==null)  return null;  
    	while (it.hasNext())
    	{
    		CompositeMap cmm = (CompositeMap)it.next();       		
			if (cmm.getName().equals("attribute")) { 
	    		if (cmm.getString("name").equals(attrib_name)) {
	    	     	try {
	    				attrib=(Attribute)DynamicObject.cast(cmm, Attribute.class);
	    			} catch (Exception e) {   
	    			} 	
	    			break;
	    		}
			}
    	}

		return attrib;
    }
    
    public void addRefElement(String prefix,String elementName){
    	CompositeMap m 		 =new CompositeMap();
    	CompositeMap tmp 		 =new CompositeMap("element");
    	tmp.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
    	
    	m = getObjectContext().getChild("complexType").getChild("all");
        
        if(m!=null){
           if (prefix!=null)
        		tmp.putString("prefix",prefix);
	       tmp.putString("ref",elementName);
	       tmp.putString("minOccurs","0");	
	       tmp.putString("maxOccurs","unbounded");	
        } 
        m.addChild(tmp);
    }
    
    public boolean hasAttribute(String attrib_name) {
    	CompositeMap m 	  = new CompositeMap();     	
        m = getObjectContext().getChild("complexType"); 
        if(m!=null){
           Iterator it = m.getChildIterator();
 	       if(it==null) return false;

 	       while(it.hasNext())
	 	   {
	 	     CompositeMap child = (CompositeMap)it.next();  
	 	     if ("attribute".equals(child.getName()) )
	 	         if (child.getString("name").equals(attrib_name)) {
	 	              return true;
	 	         }
	 	   }     
        } 
    	return false;
    }
    
    public boolean hasRefElement(String element_name) {
    	CompositeMap m 	  = new CompositeMap();  
	
        m = getObjectContext().getChild("complexType").getChild("all");   
        Iterator it = m.getChildIterator();
 	    if(it!=null)
 	       while(it.hasNext())
 	       { 	    	   
 	          CompositeMap child = (CompositeMap)it.next();  
 	          if("element".equals(child.getName()))
 	               if (child.getString("ref").equals(element_name)) {
 	            	  return true;
 	               }
 	       }            
    	return false;
    }
    
    public void removeAttribute(String attrib_name){
    	CompositeMap m 		 =new CompositeMap();
        m = getObjectContext().getChild("complexType");
        
        if(m!=null){
           Iterator it = m.getChildIterator();
 	       if(it!=null)
 	       while(it.hasNext())
 	       {
 	          CompositeMap child = (CompositeMap)it.next(); 
 	          if("attribute".equals(child.getName()))
 	               if (child.getString(SchemaFactory.KEY_NAME).equals(attrib_name)) {
 	            	  m.remove(child);
 	               }
 	       }     
        } 
    }
     
    public void save(File file) throws IOException { 
    	CompositeMap elementMap = getObjectContext();
    	//FileWriter fw = new FileWriter(file);
    	FileOutputStream fileS = new FileOutputStream(file);
    	OutputStreamWriter  fw = new OutputStreamWriter (fileS,"UTF-8");
    	try { 
			  fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			  fw.write("\r\n"); 
			  fw.write(elementMap.toXML());  
			  fw.flush();
			 } catch (IOException e) {            
			  System.err.println(e.toString());  
			 } finally { 
				fw.close(); 
			 }
    }
    
    public static void main(String[] args) throws SAXException, IOException {
    	String baseDir="C:\\Project\\FSMS\\DevPortal\\Web\\WEB-INF\\schema";
    	SchemaFactory sf=new SchemaFactory(baseDir);
    	Element el=sf.getElement("xs","attribute");
    	//System.out.println("qry is "+el.getObjectContext().toXML());
    	Attribute att= el.getAttribute("use");
    	System.out.println("b is "+ att.hasAttribValue("optional"));
    }
}

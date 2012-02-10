/*
 * Created on 2005-10-11
 */
package org.lwap.schema;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * Attribute
 * @author Zhou Fan
 * 
 */
public class Attribute extends DynamicObject {
    
    public static final int MAX_ATTRIB_VALUE = 10;

    public String getAttributeName() {
        return getString(SchemaFactory.KEY_NAME);
    }
    
    public void setAttributeName(String name){
        putString(SchemaFactory.KEY_NAME, name);
    }
    
    public boolean hasAttribValue(String attribValue) {
    	CompositeMap m 	  = new CompositeMap();     
    	m = getObjectContext().getChild("simpleType");
    	if (m==null) return false; 
      	
        m = m.getChild("restriction");      
        if (m==null) return false; 
 
           Iterator it = m.getChildIterator();
 	       if(it==null) return false;

 	       while(it.hasNext())
	 	   {
	 	     CompositeMap child = (CompositeMap)it.next();  
	 	     if ("enumeration".equals(child.getName()) )
	 	         if (child.getString("value").equals(attribValue)) {
	 	              return true;
	 	         }
	 	   }     
       
    	return false;
    }
    
    public void addAttribValue(String attribValue) {
  /*<xs:simpleType>
		<xs:restriction base="xs:NMTOKENS"> 
			<xs:enumeration value="org.lwap.application.QueryBasedService"/>
			<xs:enumeration value="org.lwap.application.XMLOutputService"/>
		</xs:restriction>
	</xs:simpleType>
	*/
    	CompositeMap m 		 = getObjectContext();
    	CompositeMap tm 	 = new CompositeMap();
        if (m.getChild("simpleType")==null) {
        	tm=new CompositeMap("simpleType");
        	tm.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
        	m.addChild(tm);
        }
        m = m.getChild("simpleType"); 
        
        if(m.getChild("restriction")==null){
        	tm=new CompositeMap("restriction");
        	tm.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
        	tm.putString("base","xs:NMTOKENS");
        	m.addChild(tm);        
        } 
      
        m = m.getChild("restriction");
        List list = m.getChilds(); 
        if (list!=null && list.size()>=MAX_ATTRIB_VALUE) {
        	return ;
        }
        	
    	tm=new CompositeMap("enumeration");
    	tm.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
    	tm.putString("value",attribValue);
    	
    	m.addChild(tm);
    }
}

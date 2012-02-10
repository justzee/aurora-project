/*
 * Created on 2005-10-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.schema;


import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

/**
 * @author Jian
 *
 */
public class SchemaListModel {
    SchemaFactory fact;

	public static final String blank_page ="blank_element.htm";
    /**
     * 
     */
    public SchemaListModel(SchemaFactory fact) {
        this.fact = fact;
    }
    
    public void onCreateModel(ProcedureRunner runner) throws IOException,SAXException {
        SchemaList sl = new SchemaList(fact.getBaseDir());
        CompositeMap context = runner.getContext();
        CompositeMap fileMap = sl.getFileListModelMap();
        
        CompositeMap model = context.getChild("model");
        if (model==null) 
        {
        	model = context.createChild("model");
        }
        
        model.addChild(fileMap);
        model.addChild(fact.categoryMap);
        //here construct a tree map 
        /*
        <element name="param" url="#" parent="">
		 <element name="child1" url="#" parent="param" >
			 <element name="child1_1" url="#" parent="child1" />
			 <element name="child1_2" url="#" parent="child1" />
			 <element name="child1_3" url="#" parent="child1" /> 
		 </element>
		 <element name="child2" url="#" parent="param" >
			 <element name="child2_1" url="#" parent="child1" />
			 <element name="child2_2" url="#" parent="child1" /> 
		 </element>			 
		 </element>
		 */
        CompositeMap treeMap=new CompositeMap("element");
        //treeMap.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
        treeMap.putString("name","schema-element");
        treeMap.putString("url",blank_page);
        treeMap.putString("parent","");        
        CompositeMap cm1=model.getChild("category-list");  
        if (cm1==null) return;  
        if (cm1.getChilds()==null) return;          
        Iterator it=cm1.getChildIterator();
        if (it==null) return;          
        while (it.hasNext())
        {
        	String categoryName="";
        	CompositeMap tmp=(CompositeMap)it.next();
        	CompositeMap categoryMap=new CompositeMap("element"); 
        	//categoryMap.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
        	categoryName=tmp.getString("name");
        	categoryMap.putString("name",categoryName);
        	categoryMap.putString("url",blank_page);
        	categoryMap.putString("parent","schema-element");     
        	
        	//--here seek the category---
        	if (fileMap!=null)
        	{           		
        		Iterator filelist=fileMap.getChildIterator();
        		if (filelist!=null)
        		{ 
        			while (filelist.hasNext())
        			{          				
            			CompositeMap filerecord=(CompositeMap)filelist.next();
            			if( filerecord==null) continue;
            			String cat= filerecord.getString("category");
            			if(cat==null){
            			    System.out.println("Warning:[SchemaListModel] 'category' not set");
            			    System.out.println(filerecord.toXML());
            			    continue;
            			}
            			if (filerecord.getString("category").equals(categoryName))
            			{                				
            				String url="elementView.service?element="+filerecord.getString("name")+
            							"&prefix="+filerecord.getString("prefix");
                			CompositeMap mm=new CompositeMap("element"); 
                			//mm.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
                        	mm.putString("name",filerecord.getString("name"));
                        	mm.putString("url",url);
                        	mm.putString("parent",categoryName);   
                       	
                        	categoryMap.addChild(mm);                          	
            			} 
        			} 
        		}
        	} 
        	if(categoryMap.getChilds()!=null){
	        	Collections.sort(categoryMap.getChilds(), new Comparator(){
	        	    
	        	    public int compare(Object o1, Object o2){
	        	        CompositeMap m1 = (CompositeMap)o1;
	        	        CompositeMap m2 = (CompositeMap)o2;
	        	        Object n1 = m1.get("name");
	        	        Object n2 = m2.get("name");
	        	        if(n1==null){
	        	            return n2==null?0:-1;
	        	        }
	        	        else
	        	            return n1.toString().compareTo(n2);
	        	    } 
	        	    
	        	    public boolean equals(Object obj){
	        	        return this.equals(obj);
	        	    }
	
	        	});
	        	//System.out.println("after sort");
	        	//System.out.println(categoryMap.toXML());
        	}
        	treeMap.addChild(categoryMap);
        }      
        model.addChild(treeMap);
    }
}

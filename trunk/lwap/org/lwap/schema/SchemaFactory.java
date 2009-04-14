/*
 * Created on 2005-10-11
 */
package org.lwap.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.core.UncertainEngine;

/**
 * Schema
 * @author Zhou Fan
 * 
 */
public class SchemaFactory  {
    
    public static final String W3C_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    public static final String NAMESPACE_PREFIX = "xs";
    
    public static final String KEY_NAME = "name";

    public static final String CATEGORY_NAME = "category";
    
    public static final String DEFAULT_EX_NAME = ".xsd";
    
    UncertainEngine		uncertainEngine;
    public	NameSpace[]	NameSpaceRegistry;
    String		        baseDir;
    File				baseDirFile;

    public Category[]   CategoryList;
    public CompositeMap	categoryMap ;
    //HashMap	 = new HashMap();
    
    public SchemaFactory(String baseDir) { 
    	this.baseDir=baseDir;
    }
    
    public SchemaFactory(UncertainEngine e){
        uncertainEngine = e;
        uncertainEngine.getObjectSpace().registerInstance(SchemaFactory.class,this);
        baseDirFile = uncertainEngine.getConfigDirectory();
    }
    
    public NameSpace getNameSpace(String uri){
        return new NameSpace(uri,NAMESPACE_PREFIX);
    }
    
    public NameSpace getNameSpaceByPrefix(String prefix){
    	return new NameSpace(W3C_SCHEMA_NAMESPACE,prefix);
    }

    /**
     * @return Returns the baseDir.
     */
    public String getBaseDir() {
        return baseDir;
    }
    /**
     * @param baseDir The baseDir to set.
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = new File(baseDirFile,baseDir).getPath();
    }
    
    /**
     * here get the exists schemaList 
     */
    public CompositeMap getShemaListMap() throws SAXException, IOException {
    	CompositeMap SchemaListMap=null;
    	CompositeMap elementMap=null; 
    	SchemaList sl=new SchemaList(baseDir);
    	SchemaListMap=sl.getFileListMap();
    	
    	Iterator   it=null;
    	Element element = null; 

    	if (SchemaListMap!=null) { 
    		it= SchemaListMap.getChildIterator();  
	    	if (it!=null) {
	    		while (it.hasNext()) {
	    			elementMap=(CompositeMap)it.next();  		
	    			element=getElement(elementMap.getString("prefix"),elementMap.getString("name"));
	 
	    			if (element!=null) { 		
	    				elementMap.addChild(element.getObjectContext());     				
	    			} else {
	    				it.remove();
	    			} 	    				  
	    		}  	    		
	    	} 	    
 	
    	} 	
    	 return SchemaListMap;
    }
    
    /**
     * parse a file map,and parse the element and attribute into the schemaListMap
     * @param fileMap
     * @param shemaListMap
     * @return
     * @throws InstantiationException 
     * @throws Exception 
     */
    public CompositeMap parseFileMap(CompositeMap fileMap,CompositeMap schemaListMap)   {
    	Element element = null; 
    	Attribute attrib = null; 
    	String elementName = "";
    	String prefix	="";
    	String attribName = "";
    	String attribValue = "";
    	boolean hasElement = false;
    	boolean hasAll = false; 
    	CompositeMap schemaParsedMap = schemaListMap ;
 
    	 if (fileMap==null) return null;
 	 
    	 elementName=fileMap.getName();	  
    	 prefix=fileMap.getPrefix(); 

    	 CompositeMap recordMap = new CompositeMap();     	 
    	 Iterator recordMapIt = schemaParsedMap.getChildIterator();   	 
    	 if (recordMapIt!=null) {
    	   	while (recordMapIt.hasNext())
     		{
     			recordMap=(CompositeMap)recordMapIt.next();
     			if (recordMap.getString("name").equals(elementName))
     			{
     				hasElement=true;
     				break;
     			}
     		}
    	 }	       
	 
    		  if (hasElement) {    	 			  
    			    //recordMap.getChild("element").setNameSpace("xs",W3C_SCHEMA_NAMESPACE);
    		      	try {
    					element = (Element)DynamicObject.cast(recordMap.getChild("element"), Element.class);
    					} catch (Exception e) { }  					
    		  }  
    		  else {  		  		 
    		  		element=createElement(elementName);  
   		  		
    		  		recordMap=new CompositeMap("record");
    		  		recordMap.putString("name",elementName);
  		  		
    		  		if (prefix==null) {prefix=""; }
    		  		recordMap.putString("prefix",prefix+"");
 
    		  		schemaParsedMap.addChild(recordMap);     		  		
    		  	}  	 	 	        

	 	 	    Iterator it = null;
 	 	        //here add attribute
 	 	       	it = fileMap.keySet().iterator();        
 	    		if (it != null) {   	    			
 	    			while (it.hasNext()) {    		
 	    				attribName=it.next().toString();  
 	    				if (!element.hasAttribute(attribName)) { 
 	    					attrib=createAttribute(attribName,"xs:string","optional",""); 
 	    					element.addAttribute(attrib); 
 	    				} else {		 	    					
 	    					attrib = element.getAttribute(attribName); 
 	    				}
    				
 	    				//here add attribute's value  
 	    				if (attrib!=null) {
 	    					attribValue=fileMap.getString(attribName);
 	    					if (!attrib.hasAttribValue(attribValue)) {					
 	    						attrib.addAttribValue(attribValue);
 	    					}
 	    				}
 	    			}  	     	    			
 	    		}            
    			    		
 	    		//here add ref element 
 	    		CompositeMap ctmap1=element.getObjectContext().getChild("complexType");
 	    		Iterator ctit=ctmap1.getChildIterator();  
	    		if (ctit!=null){
	    	    	while (ctit.hasNext())
	    	    	{ 	  	    	    		
	    	    		if (((CompositeMap)ctit.next()).getName().equals("all")) 
	    	    		{
	    	    			hasAll=true;
	    	    			break;
	    	    		}
	    	    	}
	    		}   

	    		if (!hasAll) 
	    		{   CompositeMap cc=new CompositeMap("all"); 
	    			cc.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
	    			ctmap1.addChild(cc);
	    		} 	  
 		    		
	    		it=fileMap.getChildIterator();	
 	    		if (it!=null) {	  
 	    			while (it.hasNext()) 
 	    			{
 	    			  CompositeMap child = (CompositeMap)it.next();  
 	  	 	          if (!element.hasRefElement(child.getName())) 
 	  	 	          {  	  	   	 	    	  	 	        	  
 	  	 	        	  element.addRefElement(child.getPrefix() ,child.getName());  	  	 	        	  
 	  	 	          }   	  	 	          
 	    			}	    			
 	    		} 		
    		
 	    		if (!hasElement) 
 	    		{
 	    			recordMap.addChild(element.getObjectContext());
 	    		} else
 	    		{
 	    			recordMap=element.getObjectContext();
 	    		}
 	    		 
 	        
 	        Iterator childlist=fileMap.getChildIterator();
 	        if (childlist!=null) {
 	        	while (childlist.hasNext()) {
 	        		CompositeMap cmp = (CompositeMap)childlist.next(); 
 	        		schemaParsedMap=parseFileMap(cmp,schemaParsedMap);
 	        	}
 	        }
 	         
    	return schemaParsedMap; 
    }

    /**
     * set schema map info into element files 
     * @param schemaListMap
     */
    public void setSchemaListFiles(CompositeMap schemaListMap) {
    	if (schemaListMap==null) return;	
    	String prefix="";
    	String elementName=""; 
    	Iterator it=schemaListMap.getChildIterator();
    	if (it==null)  { 
    		return ; 
    	}
    	Element element = null; 
    	while (it.hasNext()) {
    		CompositeMap recordMap = (CompositeMap)it.next();
    		prefix=recordMap.getString("prefix");
    		elementName=recordMap.getString("name");
    		try {
				element=(Element)DynamicObject.cast(recordMap.getChild("element"), Element.class);
			} catch (Exception e) { 
			} 
		    
			String file_name = "";
		    file_name=prefix+"."+elementName+DEFAULT_EX_NAME; 
		    if (prefix.equals("")) {
		    	file_name=elementName+DEFAULT_EX_NAME; 
		    }
		    if (element!=null) { 
			    File file=new File(baseDir,file_name);
			    try {
					element.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
    	} 
    }
    
    /**
     * parse a file to register the schema into a map
     * @param file
     * @param schemaListMap
     * @return CompositeMap
     * @throws IOException
     */ 
    public CompositeMap parseXMLFile(File file,CompositeMap schemaListMap) throws IOException {
    	CompositeMap fileMap=new CompositeMap();
    	FileInputStream fis =null;  

		try{
	        fis=new FileInputStream(file);
	        fileMap=CompositeMapParser.parse(fis);
	    }catch (Exception e) {
	        System.out.println("parse file failed : "+file.getName());
	        return null;
	    }finally {
	       fis.close();
	    }	  
 
	    return parseFileMap(fileMap,schemaListMap);
    }
    /**
     * parse the files'structure of the path,set it into the element schemaMap
     * 
     * @param fileDir
     * @return
     * @throws SAXException
     * @throws IOException
     */ 
    public CompositeMap getDataMap(String fileDir) throws SAXException, IOException {
    	CompositeMap schemaListMap = getShemaListMap();
    	CompositeMap tmpMap=new CompositeMap();
    	File filelist = new File(fileDir);
		File file= null;
		FileInputStream fis =null;
		for (int i =0 ;i<filelist.listFiles().length; i++) {
			file = filelist.listFiles()[i];
			if  (!file.isFile()) continue; 
			tmpMap=parseXMLFile(file,schemaListMap) ;
			if (tmpMap!=null) 
			{
				schemaListMap=tmpMap;
			}
		}
		
    	return schemaListMap;
    }
    
    public File getElementFile(String prefix,String element_name){ 
    	String file_name = "";
    	file_name=prefix+"."+element_name+DEFAULT_EX_NAME; 
    	if (prefix==null || prefix.equals("")) {
    		file_name=element_name+DEFAULT_EX_NAME; 
    	}       
        File f = new File(getBaseDir(), file_name);

    	return f;
    }
    
    public Element getElement(String prefix, String elementName) throws IOException,SAXException {
        FileInputStream fis		=null;
        CompositeMap 	cm 		= null; 
        File file=getElementFile(prefix,elementName);
        try{
        	fis=new FileInputStream(file);
        	cm=CompositeMapParser.parse(fis);
        }   catch (Exception e){
        	return null;
    	}	finally {
        	fis.close();
        }
        
        try{
            return (Element)DynamicObject.cast(cm, Element.class);
        } catch(Exception ex){
            return null;
        }
    }
    
    /**
     * @param name
     * @return
     */
    public Element createElement(String name){
       /*
    	CompositeMap m = new CompositeMap(name);
        m.setNameSpace("xs",W3C_SCHEMA_NAMESPACE);
        m.addChild(new CompositeMap("complexType"));
        try{
            return (Element)DynamicObject.cast(m, Element.class);
        } catch(Exception ex){
            return null;
        }
        */
    	CompositeMap m = new CompositeMap("element");
        m.setNameSpace("xs",W3C_SCHEMA_NAMESPACE);
        m.putString("name",name); 
        m.putString("category","UI"); 
        CompositeMap m1=new CompositeMap("complexType");
        m1.setNameSpace("xs",W3C_SCHEMA_NAMESPACE);
        m.addChild(m1);
        try{
            return (Element)DynamicObject.cast(m, Element.class);
        } catch(Exception ex){
            return null;
        }
    }
    
    public Attribute createAttribute(String name, String type, String use, String defaultValue ) {
    	CompositeMap m = new CompositeMap("attribute");
    	m.setNameSpace("xs",W3C_SCHEMA_NAMESPACE);
        m.putString("name",name);
        m.putString("type",type);
        m.putString("use",use);
        m.putString("default",defaultValue);
        try{
            return (Attribute)DynamicObject.cast(m, Attribute.class);
        } catch(Exception ex){
            return null;
        }
    }
    
    public void onInitialize(){
        if(NameSpaceRegistry==null) throw new ConfigurationError("No namespace defined");
        if(CategoryList==null) throw new ConfigurationError("No category defined");
        categoryMap = new CompositeMap("category-list"); 
        for(int i=0; i<CategoryList.length; i++){ 
    		//categoryMap.put(CategoryList[i].Name, new LinkedList());
        	CompositeMap cm=new CompositeMap("record");
        	cm.putString("name",CategoryList[i].Name);
        	cm.putString("description",CategoryList[i].Description);
        	categoryMap.addChild(cm);
		} 
        System.out.println("SchemaFactory inited "+this);
        uncertainEngine.getObjectSpace().registerInstance(SchemaFactory.class, this);
    }
 
    public static void main(String args[]) throws SAXException, IOException {
    	String baseDir="C:\\Project\\FSMS\\DevPortal\\Web\\WEB-INF\\schema";
    	SchemaFactory sf=new SchemaFactory(baseDir);
    	//CompositeMap cm=sf.getShemaListMap();
    	//System.out.println(cm.toXML());
    	//cm=sf.getShemaListMap(); 
    	//File file=new File("d:\\temp","MenuAdd.service");
    	//CompositeMap m=sf.parseXMLFile(file,cm);
    	CompositeMap mm=sf.getDataMap("C:\\Project\\FSMS\\DevPortal\\Web");
    	//CompositeMap mm=sf.getDataMap("d:\\temp");
    	//System.out.println("================================================");
    	//System.out.println(mm.toXML());
    	sf.setSchemaListFiles(mm);
    	System.out.println("===== end ======");
    	mm=sf.getDataMap("C:\\HR\\Project\\Web_cchq");
    	sf.setSchemaListFiles(mm);
    	System.out.println("===== end ======");
    	
    	mm=sf.getDataMap("C:\\HR\\Project\\Web_cchq_new");
    	sf.setSchemaListFiles(mm);
    	System.out.println("===== end ======");
    	
    	mm=sf.getDataMap("C:\\HR\\Project\\Web_nocm");
    	sf.setSchemaListFiles(mm);
    	System.out.println("===== end ======");
    	
    	mm=sf.getDataMap("C:\\HR\\Project\\Web_znuel");
    	sf.setSchemaListFiles(mm);
    	System.out.println("===== end ======");
    	
    	mm=sf.getDataMap("C:\\HR\\Project\\Web_hand");
    	sf.setSchemaListFiles(mm);    	 
    	System.out.println("===== end ======");
    }
    
}

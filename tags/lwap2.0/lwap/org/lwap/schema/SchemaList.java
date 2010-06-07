package org.lwap.schema;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.javautil.file.WildcardFilter;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;

public class SchemaList {

	public String baseDir;
	public SchemaList(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public CompositeMap getFileListModelMap() throws IOException, SAXException { 
		SchemaFactory sf=new SchemaFactory(baseDir);
		Element element = null;
		CompositeMap cm=getFileListMap();
		Iterator it=cm.getChildIterator(); 
		CompositeMap cc=null;

		if (it!=null) {
			while (it.hasNext())
			{
				cc=(CompositeMap)it.next();
				String prefix = cc.getString("prefix");
				String elementName = cc.getString("name");
				element = sf.getElement(prefix,elementName);	
				if (element!=null && element.getCategoryName()!=null)
					cc.putString("category",element.getCategoryName());
			}
		}
		return cm;
	}

	public CompositeMap getFileListMap(){
		WildcardFilter wf=new WildcardFilter("*"+SchemaFactory.DEFAULT_EX_NAME);
		File filelist = new File(baseDir);
		if(!filelist.exists()) throw new IllegalArgumentException("baseDir "+baseDir+" not exist");
		File file= null;
		String filename  =null; 
		String elementName =null;
		String prefixName =null;
		CompositeMap cm = new CompositeMap("schema-list");
		cm.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
		CompositeMap m = null; 
		
		for (int i =0 ;i<filelist.listFiles(wf).length; i++) {
			file = filelist.listFiles(wf)[i];
			m=new CompositeMap("record");
			m.setNameSpace("xs",SchemaFactory.W3C_SCHEMA_NAMESPACE);
			filename=file.getName(); 
			elementName=filename.substring(0,filename.length()-SchemaFactory.DEFAULT_EX_NAME.length());
			 
			prefixName="";
			if (elementName.indexOf(".")>=0) { 
				prefixName=elementName.substring(0,elementName.indexOf(".")); 
				elementName=elementName.substring(elementName.indexOf(".")+1); 
			}
			m.putString("name",elementName); 
			m.putString("prefix",prefixName);
			cm.addChild(m);			
		}
		
		return cm;
	}
 
	public static void main(String args[]) throws IOException, SAXException {
		SchemaList sl=new SchemaList("C:\\schemas");
		/*
		CompositeMap sm=sl.getFileListMap();
		System.out.println("sm is "+sm.toXML());
		 
		 try {
		  // Create file objects
		  FileOutputStream fout = new FileOutputStream("d:\\temp\\abc.txt");
		  BufferedOutputStream bout = new BufferedOutputStream(fout);
		  DataOutputStream dout = new DataOutputStream(bout);
		  // Write data to file in this order:
		  dout.writeBytes(sm.toXML()); 

		  dout.flush();
		  fout.close();
		 } catch (IOException e) {            // Trap exception
		  System.err.println(e.toString());   // Display error
		 }
		 */
		 
			System.out.println(sl.getFileListMap().toXML());
			System.out.println(sl.getFileListModelMap().toXML());
		 
	}
	
}

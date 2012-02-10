/**
 * Created on: 2002-11-25 17:17:29
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

/**
 * Configuration of View
 */

public class ViewConfig {
	
    public static final String CONFIG_PATH = "config.xml";  	
//    public static final String KEY_IMAGE_PATH = "image-path";
    public static final String KEY_STYLE_SHEET = "style-sheet";
    public static final String DEFAULT_STYLE_SHEET = "style.css";    

	CompositeLoader composite_loader;	
    CompositeMap    properties;    
    String			web_path;
    File			resource_path;
    
    // built-in properties
    String			style_sheet_name;
    String			image_path;
    String			script_path;
    
    public ViewConfig( String web_path, String resource_path)throws FileNotFoundException {
    	this(web_path, new File(resource_path));
    }
    
    public ViewConfig( String web_path, File resource_path) throws FileNotFoundException {
    	if( !resource_path.exists()) throw new FileNotFoundException();
    	this.setResourcePath(resource_path);
    	this.setWebPath(web_path);
    	this.composite_loader = new CompositeLoader( resource_path.getPath() ,null);

    	try{
    		loadProperties(CONFIG_PATH);
    		style_sheet_name = this.getParameter(KEY_STYLE_SHEET);
    		
    	} catch(Exception ex){
    		properties = new CompositeMap();
    	}
    	
    }
    
    public void loadProperties(String config) throws IOException, SAXException {
    	properties = composite_loader.load(config);
    }
    

    public void setResourcePath( File path){
    	this.resource_path = path;
    }

    public File getResourcePath(){
    	return resource_path;
    }    
    
    public void setWebPath(String path){
    	if( path == null) return;
    	int idx = path.length()-1;
    	if( idx>=0)
    		if(path.charAt(idx) != '/') path += '/';
    	this.web_path = path;
    }
    
    public String getWebPath(){
    	return this.web_path;
    }

    /** get web url for a resource such as image */    
	public String getResourceURL(String resource_name){
		return this.web_path == null? resource_name:web_path + resource_name;
	}
	
	/** get physical path for a resource */
	public File getResourcePath(String resource_name){
		return new File( resource_path, resource_name);
	}  
	
	public InputStream getResourceAsStream( String resource_name){
		File file = getResourcePath(resource_name);
//		System.out.println(file);
		if( file == null) return null;
		if( file.exists())
			try{
				return new FileInputStream( file);
			}catch(FileNotFoundException ex){
				return null;
			}
		else
			return null;
	}  
	
	public String getParameter( String param_name){
		return properties == null? null: (String) properties.get(param_name);
	}
	
	public CompositeMap getConfig(){
		return properties;
	}
	
	public CompositeMap getConfigSection( String name){
		return properties == null? null: properties.getChild(name);
	}
	
	
	public String getStyleSheetURL(){
		return getResourceURL( this.style_sheet_name);		
	}
	
/*	
	public String getImageURL(String image_name){
		
	}
*/	

}

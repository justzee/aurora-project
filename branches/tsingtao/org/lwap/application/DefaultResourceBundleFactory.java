/**
 * Created on: 2004-3-9 17:01:40
 * Author:     zhoufan
 */
package org.lwap.application;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * 
 */
public class DefaultResourceBundleFactory implements ResourceBundleFactory {
    
    // String -> Locale
    Map         mLocales = new HashMap();
	
	public static class DefaultResourceBundle extends ResourceBundle {
		
		
		public  Enumeration getKeys()  {
			return new Enumeration() {
				
				public boolean hasMoreElements() {
					return false;
				}
				
				public Object nextElement() {
					return null;
				}
 
			};
		}
		
		protected Object handleGetObject(String key){
				return key;
		}
			
	};
	
	public static DefaultResourceBundle default_bundle = new DefaultResourceBundle();

	
	public class ResourceBundleAdaptor extends ResourceBundle {

		ResourceBundle actual_bundle;
		String		   source_file;
		
		public String toString(){
			return "ResourceBundle@"+actual_bundle.getLocale();
		}
		
		public ResourceBundleAdaptor( ResourceBundle bundle){
			this.actual_bundle = bundle;
			//setParent(default_bundle);
		}
		
		public  Enumeration getKeys()  {
			return actual_bundle.getKeys();
		}
		
		protected Object handleGetObject(String key){
			try{
				return actual_bundle.getObject(key);
			} catch( MissingResourceException ex){
				return key;
			} 
		}
	
		
	}

	
	public String file_name;

	/**
	 * Constructor for DefaultResourceBundleFactory.
	 */
	public DefaultResourceBundleFactory(String file_name) {
		this.file_name = file_name;
	}

	/**
	 * @see org.lwap.application.ResourceBundleFactory#getResourceBundle(Locale)
	 */
	public ResourceBundle getResourceBundle(Locale locale) {
		try{
			ResourceBundle bundle = ResourceBundle.getBundle(file_name,locale);
			if( bundle != null){
				return new ResourceBundleAdaptor(bundle);
			}else{
				return null;
			}
		} catch(MissingResourceException ex){
			System.out.println("Can't get resource bundle for locale "+locale);
			return ResourceBundle.getBundle(file_name,Locale.getDefault());
		} 
	}
	
	public Locale  getLocale( String code ){
	    Locale l = (Locale)mLocales.get(code);
	    if(l==null){
	        l = new Locale(code);
	        mLocales.put(code, l);
	    }
	    return l;
	}
	
	public static void main(String[] args) throws Exception {
		DefaultResourceBundleFactory fact = new DefaultResourceBundleFactory("org.lwap.application.prompt");
		ResourceBundle bd = fact.getResourceBundle(Locale.CHINESE);
		System.out.println(bd.getString("prompt.yes"));
	}


}

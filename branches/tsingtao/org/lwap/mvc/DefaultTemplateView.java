/**
 * Created on: 2002-11-20 14:12:20
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import uncertain.composite.CompositeMap;

public abstract class DefaultTemplateView extends TemplateView {
	
	public static final String TEMPLATE_EXT = ".template";
	
	static Class[] param_declare = { BuildSession.class, CompositeMap.class, CompositeMap.class};
	
	static HashMap method_cache = new HashMap(); //( Class -> HashMap( tag -> Method ))
	
	/** return the template name, with no path nor extension.
	 *  this method by defaults return instance's class name
	 */
	protected String getTemplateName(){
		return this.getClass().getName();
	}
		
	protected void loadDefaultTemplate() throws IOException {
		String tp_name = getTemplateName().replace('.','/') + TEMPLATE_EXT;
//		System.out.println(tp_name);
		InputStream stream = null;
		try{
			stream = DefaultTemplateView.class.getClassLoader().getResourceAsStream(tp_name);
			if( stream == null ) throw new IOException ("DefaultTemplateView: Can't load template " + tp_name);
			StringBuffer buf = new StringBuffer();
			int ch;
			while( (ch = stream.read()) != -1){
				buf.append((char)ch);
			}
			setTemplate(buf.toString());
		} finally{
			if(stream != null) stream.close();
		}			
	}
	
	public DefaultTemplateView()  {
		try{
			loadDefaultTemplate();
		}catch(IOException ex){
			ex.printStackTrace(System.err);			
		}
	}
	
	public static Method getMethod( Object instance, String tag) {
		
		Class cls = instance.getClass();
		HashMap clsMap = (HashMap)method_cache.get(cls);
		if( clsMap == null){
			clsMap = new HashMap(30);
			method_cache.put(cls,clsMap);
		}
		Method method = (Method)clsMap.get(tag);

		if(method == null){
			try{
//				System.out.println(instance.getClass().getName());
				method = (Method)cls.getMethod("get"+tag, param_declare);
			} catch(Throwable thr){
//				thr.printStackTrace();
			}
			if( method != null) clsMap.put(tag,method);
			else return null;
		}
		return method;
	}
	

	/**
	 * Invoke derived class's get<TagName>(session, model, view) method via java reflection
	 */
	public String getTagContent(
		String tag,
		BuildSession session,
		CompositeMap model,
		CompositeMap view)  throws ViewCreationException 
	{
		Method m = getMethod( this, tag);
		if( m == null) return tag;
		try{
			Object obj = m.invoke(this,new Object[]{session,model,view});
			//return obj == null? tag:(String)obj;
			return (String)obj;
		}catch(Throwable thr){
			return tag;
		}
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public abstract String getViewName();


}

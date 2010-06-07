/*
 * DefaultViewBuilder.java
 *
 * Created on 2002年1月13日, 下午2:01
 */

package org.lwap.mvc;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 *
 * @author  Administrator
 * @version 
 */
public class DefaultViewFactory implements ViewFactory {
	

	public static final String DEFAULT_NAMESPACE_URL = "http://mvc.lwap.org";
	
    
    static DefaultViewFactory inst = new DefaultViewFactory();

	ViewFactoryStore _store;
	
    public void setViewFactoryStore(ViewFactoryStore store){
    	_store = store;
    }

    public ViewFactoryStore getViewFactoryStore(){
    	return _store;    
    }
    
    public void populateView( CompositeMap model, CompositeMap view ){
    	Iterator it = view.entrySet().iterator();
    	while( it.hasNext()){
    		Map.Entry entry = (Map.Entry)it.next();
    		Object value = entry.getValue();
    		if( value instanceof String){
    			String str_value = (String)value;
    			if( str_value.indexOf('$')>=0){ 
    				view.put( entry.getKey(), TextParser.parse(str_value, model));
    			}
    		}
    	}
    	String text = view.getText();
    	if(text!=null){
    	    text = TextParser.parse(text,model);
    	    view.setText(text);
    	}
    	
    }

    String getParsedContent(String text, CompositeMap model){
        if( text.indexOf('$')>=0)
            return TextParser.parse(text,model);
        else
            return text;
    }

    public View createView(BuildSession session, String view_name, CompositeMap model, CompositeMap view) throws ViewCreationException {
    	
    	return  new View(){
    	    
    		public void build( BuildSession session, CompositeMap model, CompositeMap view) 
    		throws ViewCreationException {
    		    if( view == null || session==null) return;
    		    String close_tag = "</" + view.getName() + ">";
    		    Writer out = session.getWriter();
    		    try{
//                    out.flush();
    		        out.write('<');
    		        out.write(view.getName());
    		        if(view.size()>0){			// print attributes
    		            for(Iterator it = view.entrySet().iterator(); it.hasNext();){
    		                Map.Entry entry = (Map.Entry)it.next();
    		                Object key = entry.getKey();
    		                if(key==null) continue;
    		                out.write(' ');
    		                out.write(key.toString());
    		                out.write("=\"");
    		                Object value = entry.getValue();
    		                if(value!=null)
    		                    out.write(getParsedContent(value.toString(),model));
    		                out.write('\"');    		                
    		            }
    		        }
    		        out.write('>');
    		        Collection childs = view.getChilds();
    		        if(childs!=null){			// print childs
    		            session.applyViews(model, childs);
    		            out.write(close_tag);
    		        }else{
    		            String text = view.getText();
    		            if(text!=null){ 
    		                out.write(getParsedContent(text,model));
    		                out.write(close_tag);
    		            }
    		            /*
    		            else
    		                out.write(close_tag);
    		                */
    		            
    		        }
    		        out.flush();
    		    }catch(IOException ex){
    		        
    		    }
    		    
    		}
    	    
/*    		
    		public void build( BuildSession session, CompositeMap model, CompositeMap view) throws ViewCreationException {
		    	if( view == null || session==null) return;
		        try{
		        	CompositeMap vc = (CompositeMap)view.clone();
		        	populateView(model,vc);
		        	Writer out = session.getWriter();
		        	out.write( vc.toXML());
		        	out.flush();
		        } catch(Exception ex){
		            throw new ViewCreationException(ex);
		        }		    			
    		}
*/    		
    		public String getViewName(){
    			return "DefaultView";
    		}
    		
    	};
    }    

    public  String getNamespaceURL(){ return DEFAULT_NAMESPACE_URL ; }
    
    
    public static DefaultViewFactory getInstance(){
        return inst;
    }
    
}

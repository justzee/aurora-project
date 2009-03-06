/*
 * TransformSession.java
 *
 * Created on 2002年1月12日, 下午10:41
 */

package org.lwap.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.lwap.application.BaseService;

import uncertain.composite.CompositeMap;


/**
 *
 * @author  Administrator
 * @version 
 */
public class BuildSession {

	protected int				   unique_id = 0;
	protected Writer              writer;
	protected ViewFactoryStore    factory_store;
	protected ViewFactory			view_factory;
   	
   	// properties to share among views
    Map					properties;

	// set of resources that already included in current page.
    Set					included_resources;

    // context path for current view
    String				current_context_path;
    
    BaseService		    service;
    
    boolean             auto_flush = false;
    
    // id of child in model/view
    int current_model_id = 0;
    int current_view_id = 0;
    
    // cache of Format instance
    static HashMap  	 stored_format = new HashMap(10);
    static DateFormat	 default_date_fmt = new SimpleDateFormat("yyyy-MM-dd");
    static NumberFormat default_num_fmt = new DecimalFormat();

	public BuildSession(){
	}    
    
    public BuildSession( ViewFactoryStore store, Writer writer){
        factory_store = store;        
        this.writer = writer;
    }
    
    public int getCurrentModelID(){
    	return current_model_id;
    }
    
    public int getCurrentViewID(){
    	return current_view_id;
    }
    
    public int getUniqueID(){
    	return unique_id++;
    }
    
    public ViewFactory getCurrentViewFactory(){
    	return view_factory;
    }
    
    public ViewFactoryStore getViewFactoryStore(){
        return factory_store;
    }
   
    
	/**
	 * get Writer object to put view content
	 * @return Writer for output
	 */
    public Writer getWriter(){
        return writer;
    }
    
	/**
	 * Method get a PrintWriter version of Writer
	 * @return PrintWriter for output
	 */
    public PrintWriter getPrintWriter(){
        return new PrintWriter( writer);
    }
    
    public void endSession(){
       try{ 
       		if(included_resources != null) included_resources.clear();
       		if(properties != null) properties.clear();       	
	        if( writer != null){
	            writer.flush();
	            //writer.close();
	        }
       } catch(IOException ex){
       		ex.printStackTrace();
       }
    }
    
	
	public void setService( BaseService svc ){
		this.service = svc;
	}
	
	public BaseService getService()  {
		return this.service;
	} 
    
	/**
	 * get a localized string by this BuildSession's locale info
	 * @param key
	 * @return String
	 */
    public String getLocalizedString(String key){
    	//return ls_provider ==null? key: ls_provider.getLocalizedString(key);
    	return this.service.getLocalizedString(key);
    }
    
    public Object getProperty(Object key){
    	
    	return this.properties==null?null:this.properties.get(key);
    }
    
    public void setProperty(Object key, Object value){
    	if( this.properties == null) this.properties =new HashMap(50);
    	this.properties.put(key,value);
    }
    
    public void setCurrentContext( String path){
    	this.current_context_path = path;
    }
    
    public String getCurrentContext(){
    	return this.current_context_path;
    }
    
    public String getLocalResource(String resource_name){
    	return this.current_context_path + resource_name;
    }
    
    public String includeResource( String resource_name){
    	String resource_url = getLocalResource(resource_name);
    	if(this.included_resources == null) this.included_resources = new HashSet();
    	if( !included_resources.contains(resource_url)){
    		included_resources.add(resource_url);
    		return resource_url;
    	} else
    		return null;
    }

    public void includeScript(String script_name, String script_type) throws IOException {
        includeScript(script_name,script_type,false);
    }

    public void includeScript(String script_name, String script_type, boolean defer) throws IOException {
    	String script_url = includeResource(script_name);
    	if( script_url == null) return;    	
        if(auto_flush) writer.flush();
    	writer.write("<script language=\"");
    	writer.write(script_type);
    	writer.write("\" src=\"");
    	writer.write(script_url);
    	writer.write("\" ");
    	if(defer)
    	    writer.write("DEFER ");
    	writer.write("></script>\r\n");
    	writer.flush();
    }
    
    public void includeScript(String script_name) throws IOException {
    	includeScript(script_name,"javascript");
    }
    
    public void includeScriptVariable(String var_name, String var_value ) throws IOException {
    
    	String resource = includeResource( var_name);
    	if( resource == null) return;
        if(auto_flush) writer.flush();
    	writer.write("<script language=\"javascript\">");
    	writer.write("  var "+ var_name +" = '" + var_value+ "';");
    	writer.write("</script>");    
    	writer.flush();
    }
    
    
    public void includeStyleSheet( String sheet_name) throws IOException {
    	String script_url = includeResource(sheet_name);
    	if( script_url == null) return;   
        if(auto_flush) writer.flush();
    	writer.write("<link href=\"");
    	writer.write(script_url);
    	writer.write("\" rel=\"stylesheet\" type=\"text/css\">\r\n");
    	writer.flush();
    }
    
    public DateFormat getDateFormat( String format){
    	if( stored_format.containsKey(format)) 
    		return (DateFormat)stored_format.get(format);
    	SimpleDateFormat fmt = new SimpleDateFormat(format);
    	stored_format.put(format, fmt);
    	return fmt;
    }
    
    public NumberFormat getNumberFormat( String format){
    	if( stored_format.containsKey(format)) 
    		return (NumberFormat)stored_format.get(format);
    	DecimalFormat fmt = new DecimalFormat(format);
    	stored_format.put(format, fmt);
    	return fmt;
    }    
    
    /**
     * print value of object, with specified format
     */
    public String toDisplayString( String format, Object obj) throws IOException {
    	if( obj == null) return "";
    	String output = null;
    	if( obj instanceof Date){
    		DateFormat df = getDateFormat(format);
    		output = df.format((Date)obj);
    	}else if ( obj instanceof Number){
    		NumberFormat nf = getNumberFormat(format);
    		output = nf.format(obj); 
    	} else output = obj.toString();
    	
    	return output;
    	    	
    }
    
    public String toDisplayString( Object obj) throws IOException {
    	if( obj == null) return "";
    	String output = null;
    	if( obj instanceof Date){
    		output = default_date_fmt.format((Date)obj);
/*
    	}else if ( obj instanceof Number){
    		output = default_num_fmt.format(obj); 
*/
    	}     	
    	else output = obj.toString();
    	    	
    	return output;
    }
    
    
    // internal method
    void buildView( View view_inst, CompositeMap model, CompositeMap view) throws ViewCreationException{
    	if( view_inst == null) return;
        if(auto_flush) try{
            writer.flush();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        view_inst.build(this,model,view);
    }
    
    
    /** build a certain view specified by view_name and url */
    public void buildView( String url, String view_name,  CompositeMap model, CompositeMap view ) throws ViewCreationException {
       view_factory = factory_store.getViewFactory( url );
       buildView( view_factory.createView(this,view_name,model,view), model, view);
    }
    
    /** build a single view on a single model by view name */
    public void buildView( String view_name, CompositeMap model, CompositeMap view)throws ViewCreationException {
       view_factory = factory_store.getDefaultFactory();
       buildView( view_factory.createView(this,view_name,model,view), model, view);
    }
    
    /** build a single view on a single model */
    public void buildView( CompositeMap model, CompositeMap view ) throws ViewCreationException {
        String ns_uri = view.getNamespaceURI();
        if( ns_uri != null)
            view_factory = factory_store.getViewFactory( ns_uri );
        else
            view_factory = factory_store.getDefaultFactory();
        
        if( view_factory != null){ 
            if(auto_flush) 
                try{
                    writer.flush();
                }catch(IOException ex){
                    ex.printStackTrace();
                }       		
            buildView( view_factory.createView(this,view.getName(),model,view), model, view);
        }
    }
    
    /** build a single view on a collection of model */
    public void buildViews(  String view_name,  Collection clModel, CompositeMap view ) throws ViewCreationException {
        if( clModel == null) return;
        Iterator it = clModel.iterator();
        this.current_model_id=0;
        while(it.hasNext()){
            CompositeMap model = (CompositeMap) it.next();
            int tmp = current_model_id;
            buildView(  view_name, model, view);
            current_model_id = tmp;
            current_model_id++;
        }        
    }    
    
    /** build a single view on a collection of model, with ViewBuilder url specified */
    public void buildViews( String url, String view_name,  Collection clModel, CompositeMap view ) throws ViewCreationException {
        if( clModel == null) return;
        Iterator it = clModel.iterator();
        this.current_model_id=0;
        while(it.hasNext()){
            CompositeMap model = (CompositeMap) it.next();
            int tmp = current_model_id;
            buildView( url, view_name, model, view);
            current_model_id = tmp;
            current_model_id++;
        }        
    }
    
    /** build view with specified view for each child in specified model*/
    public void buildViews( Collection clModel, CompositeMap view ) throws ViewCreationException {
        if( clModel == null) return;
        Iterator it = clModel.iterator();
        this.current_model_id=0;
        while(it.hasNext()){
            CompositeMap model = (CompositeMap) it.next();
            int tmp = current_model_id;
            buildView( model, view);
            current_model_id = tmp;
            current_model_id++;
        }
    }
    
    /** build a collection of view on a collection of model */
    public void apply( Collection clModel, Collection clView) throws ViewCreationException {
    	if( clModel == null || clView == null) return;
        Iterator it = clModel.iterator();
        this.current_model_id = 0;
        
        while(it.hasNext()){
            CompositeMap model = (CompositeMap) it.next();
            int tmp = current_model_id;
            applyViews( model, clView);
            current_model_id = tmp;
            current_model_id++;
        }
    	
    }
    
    /** build view with specified model for each view in views collection */
    public void applyViews( CompositeMap model, Collection views) throws ViewCreationException{
    	if( views == null) return;
    	this.current_view_id = 0;
        Iterator it = views.iterator();
        while(it.hasNext()){
                  CompositeMap view = (CompositeMap) it.next();
                  int tmp = current_view_id;
                  buildView( model, view);
                  current_view_id = tmp;
                  current_view_id++;
        	}
    }

    /**
     * @return the auto_flush
     */
    public boolean isAutoFlush() {
        return auto_flush;
    }

    /**
     * @param auto_flush the auto_flush to set
     */
    public void setAutoFlush(boolean auto_flush) {
        this.auto_flush = auto_flush;
    }
    

}

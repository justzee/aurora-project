/**
 * Created on: 2002-11-17 16:28:50
 * Author:     zhoufan
 */
package org.lwap.ui.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.lwap.application.BaseService;
import org.lwap.mvc.BuildSession;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.Layout;
import org.lwap.mvc.servlet.JspViewFactory;
import org.lwap.ui.InputField;
import org.lwap.ui.InputFieldImpl;
import org.lwap.ui.UIAttribute;
import org.lwap.validation.ParameterParser;
import org.lwap.validation.ParameterSource;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.IterationHandle;



public class Form extends DynamicObject {
	
	public static final String FORM				= "form";
	public static final String ACTION 			= "Action";
	public static final String INPUT 			= "input";
	public static final String PARAMETER			= "parameter";	
	public static final String FORM_PARAMETERS	= "form-parameters";
	public static final String FORM_SECTION		= "form-section";	
	public static final String FORM_MESSAGES		= "form-messages";
	public static final String FORM_MESSAGE 		= "form-message";	

	public static final String KEY_ERROR_PROMPT   = "ErrorPrompt";
	public static final String KEY_ENTITY 		= "Entity";
	public static final String KEY_INPUT_TYPE 	= "Type";
	public static final String KEY_TEXT = "Text";
	public static final String KEY_SHOW_COMMIT_BUTTON = 	"ShowCommitButton";
	public static final String KEY_SUBMIT_PROMPT 	=   "SubmitPrompt";
	public static final String KEY_RESET_PROMPT		=	"ResetPrompt";
	public static final String KEY_SHOW_ERROR_IN_HEAD = "ShowErrorInHead";
	public static final String KEY_PARAMETER_SOURCE_PREFIX = "ParamSourcePrefix";
    
    public static final String KEY_ALLOW_REPEATED_SUBMIT = "AllowRepeatedSubmit";
	
	public static final String KEY_CURRENT_FORM_NAME = "CURRENT_FORM_NAME";
	
	public static String getInputFieldName( BuildSession _session, String name){
		String form_name = (String)_session.getProperty(Form.KEY_CURRENT_FORM_NAME);
		return form_name == null? name: form_name +'.' + name;
	}
	
	public static String getInputFieldName( BuildSession _session, InputField fld){
		return getInputFieldName(_session, fld.getParameterName());
	}
	
	
	HashMap 	input_params;
	BaseService service;
	String		para_source_prefix = "@";
	
	public void setParamSourcePrefix(String pr){
		para_source_prefix = pr;
	}
	
	public String getFormName(){
		//return "FRM_" + getString( UIAttribute.ATTRIB_NAME, "MAIN_FORM");
	    return getString( UIAttribute.ATTRIB_NAME, "MAIN_FORM");
	}
	
	public String getWidth(){
		return getString( UIAttribute.ATTRIB_WIDTH, "75%");
	}
	
	public String getAction(){
		return getString( ACTION, "#");
	}
	
	public String getEntity(){
		String entity = getString( KEY_ENTITY, getString(DataBindingConvention.KEY_DATAMODEL));
		return entity == null?null:entity.toUpperCase();
	}
	
	public boolean isShowCommitButton(){
		return getBoolean(KEY_SHOW_COMMIT_BUTTON,true) && !isReadOnly();
	}
	
	public boolean isShowErrorInHead(){	    
	    return getBoolean(KEY_SHOW_ERROR_IN_HEAD, false);
	}

	public void setService( BaseService s){
		this.service = s;
	}
	
	public boolean isReadOnly(){
		return getBoolean( UIAttribute.ATTRIB_READ_ONLY,false);
	}
	
	public void setReadOnly(boolean r){
		putBoolean(UIAttribute.ATTRIB_READ_ONLY,r);
	}
	
	
	/**
	 * @see uncertain.composite.DynamicObject#initialize(CompositeMap)
	 */
	public DynamicObject initialize(CompositeMap context)
		throws ClassCastException {
		return super.initialize(context);
	}
	
	public void initForm(){
	    
		// create HashMap with default size
		input_params = new HashMap(40);

		// set default error message display style
		CompositeMap context = this.getObjectContext();

		if(context.get(Layout.KEY_TEMPLATE)!=null && context.get(Form.KEY_SHOW_ERROR_IN_HEAD) == null){
			context.putBoolean(Form.KEY_SHOW_ERROR_IN_HEAD, true);
		}
		
		if(context.get(KEY_PARAMETER_SOURCE_PREFIX)!=null)
		    setParamSourcePrefix(context.getString(KEY_PARAMETER_SOURCE_PREFIX));
		
		IterationHandle handle = new IterationHandle(){
			
    		public int process( CompositeMap map){
				
				String entity = getEntity();    			
    			String name = map.getName();
    			boolean read_only = isReadOnly();
    			
    			if(name==null) return IterationHandle.IT_CONTINUE;
    				// for each input field
    				if( name.equals(INPUT) && map.getNamespaceURI()!=null){
    					String field_name = map.getString(UIAttribute.ATTRIB_NAME);
    					if( field_name != null){
    						input_params.put(field_name, map);

    						// change view type
    						String type = map.getString(KEY_INPUT_TYPE);
    						if( type != null){ 
    							map.setName(type);
    							map.remove(KEY_INPUT_TYPE);
    						}else
    							throw new IllegalArgumentException("Form:must specify 'Type' for input field:"+map.toXML());

    						// set default prompt
    						if( map.get( UIAttribute.ATTRIB_PROMPT) == null && entity != null)
    							map.put( UIAttribute.ATTRIB_PROMPT, entity + '.' + map.get(UIAttribute.ATTRIB_NAME));

    						// set dataField
    						if( map.get(DataBindingConvention.KEY_DATAFIELD)==null)
    							map.put(DataBindingConvention.KEY_DATAFIELD, para_source_prefix + field_name);	
    							
    						// set readonly
    						if( !map.containsKey(UIAttribute.ATTRIB_READ_ONLY) && read_only){
    							map.put(UIAttribute.ATTRIB_READ_ONLY, getObjectContext().getBoolean(UIAttribute.ATTRIB_READ_ONLY));
    						}
    						
    					}
    					else throw new IllegalArgumentException("Form:must specify name for input field:"+map.toXML());
    				}else if(name.equals(FORM_PARAMETERS)){
    				  Iterator cit = map.getChildIterator();
    				  if( cit != null)
    				  while( cit.hasNext())
    				  {
    				  	CompositeMap param = (CompositeMap) cit.next();
    					String param_name = param.getString(UIAttribute.ATTRIB_NAME);
    					//System.out.println("analyze "+param_name);
    					if( param_name != null ){ 
        					if(input_params.containsKey(param_name)){
        					    System.out.println("Warning:duplicate parameter, "+param_name+" already exists, previous:");
        					    System.out.println(((CompositeMap)input_params.get(param_name)).toXML() );
        					    System.out.println("New:");
        					    System.out.println(param.toXML());
        					}
    					    input_params.put(param_name,param);
    					    //if("group".equals(param.getName())) System.out.println("added group "+param_name);
    					}    					
    				  }
    				  return IterationHandle.IT_NOCHILD;
    				}
    			return IterationHandle.IT_CONTINUE;	
    		}
			
		};
		
		context.iterate(handle,true);
		

	}
	
	public HashMap getInputFields(){
		return input_params;
	}
	
	public void createFormMessage(String msg){
	    createFormMessage(msg,false);
	}
	
	public void createFormMessage(String msg, boolean first){
		CompositeMap map = JspViewFactory.createView(FORM_MESSAGE);
		map.put(KEY_TEXT,msg);
		CompositeMap msgs = getObjectContext().getChild(FORM_MESSAGES);
		if( msgs == null){
			msgs = JspViewFactory.createView(FORM_MESSAGES);
			msgs.put( UIAttribute.ATTRIB_NAME, getFormName() + "_MESSAGES");
			msgs.setParent(getObjectContext());
			((LinkedList)getObjectContext().getChildsNotNull()).addFirst(msgs);
		}
		if(first){
		    msgs.getChildsNotNull().add(0,map);
		    map.setParent(msgs);
		}
		else
		    msgs.addChild(map);
	}
	
	public void setError( ValidationException ex){
		if(ex.getErrorType()!=null){
		/*	
		    String msg=ex.getParameterPrompt();
			if(msg==null ){  
			    if(ex.getInputValue()!=null)
			        msg = ex.getInputValue().toString();
			    else 
			        msg = ex.getParameterName();
			}
			setErrorPrompt( msg, service.getValidationMessage(ex) );
		*/
		    setErrorPrompt(ex.getParameterName(), service.getValidationMessage(ex));
		}
		
	}
	
	public void setErrorPrompt(String field_name, String message){
		CompositeMap field = (CompositeMap)input_params.get(field_name);
		if(!isShowErrorInHead()){
			if( field != null)
				field.put(KEY_ERROR_PROMPT , message);
			else
				createFormMessage(message);
		}else{
		    String msg=null;
		    if(field!=null){ 
		        msg = field.getString("Prompt");
			    msg = service.getLocalizedString(msg);
		    }
		    else if(field_name!=null){
		        msg = service.getLocalizedString(field_name);
		    }
		    if(msg!=null) msg = msg+':'+message;
		    else msg=message;
		    createFormMessage(msg);
		    //System.out.println(msg);
		}
	}
	
	/**
	 * populate form input on parse failure.
	 * @param param_source  source for parameter
	 */
	public void populateFormWithInput( ParameterSource param_source ){
		// populate input fields with original input
		Iterator fld = input_params.values().iterator();
		while( fld.hasNext()){
			CompositeMap field = (CompositeMap) fld.next();
			String value = param_source.getParameter(field.getString(UIAttribute.ATTRIB_NAME));
			if( value != null){
				field.put(DataBindingConvention.KEY_DATAVALUE, value );
            }
		}        
	}


	/**
	 * Parse parameter from a ParameterSource, sets error prompt & populate form
	 * from input on parse failure.
	 * @param param_source  source for parameter
	 * @param param_holder  place to put parsed parameters
	 * @return true if no parse error occurs
	 */
	public boolean parseParameter( ParameterSource param_source, CompositeMap param_holder){
		
	
		boolean parse_success = true;
		Collection fields = input_params.values();
		/*
		for(Iterator it = fields.iterator(); it.hasNext();){
		    CompositeMap p = (CompositeMap)it.next();
		    System.out.println("Param "+p.toXML());
		}
		*/
		Collection parse_error = ParameterParser.parseParameter(param_source,fields,param_holder);

		if( parse_error != null){
			parse_success = false;

			// set error prompt
			Iterator it = parse_error.iterator();
			while( it.hasNext()){
				ValidationException exp = (ValidationException)it.next();
				String error = this.service.getValidationMessage(exp);
				if( error != null)
				    setError(exp);
				    //setErrorPrompt( exp.getParameterName(), error);				
			}
		
		}
		
	
		return parse_success;
	}
	
	public CompositeMap getFormParameters(){
		CompositeMap form_params = getObjectContext().getChild(FORM_PARAMETERS);
		if( form_params == null){ 
			form_params = JspViewFactory.createView(FORM_PARAMETERS);
			getObjectContext().addChild(form_params);
		}	
		
		return form_params;	
	}
	
	// create a parameter in form 
	public CompositeMap createParameter( String name, Class data_type, boolean nullable, Object default_value)
	{
		CompositeMap form_params = getFormParameters();
		CompositeMap param = InputFieldImpl.createParameter(name,data_type,nullable,default_value);
		form_params.addChild(param);
		return param;
	}
	
	public void addParameter( CompositeMap param){
		CompositeMap form_params = getFormParameters();
		form_params.addChild(param);
	}
    
    public Boolean getAllowRepeatedSubmit(){
        return getBoolean(KEY_ALLOW_REPEATED_SUBMIT);
    }
    
    public void setAllowRepeatedSubmit( Boolean b ){
        put(KEY_ALLOW_REPEATED_SUBMIT, b);
    }
	

}

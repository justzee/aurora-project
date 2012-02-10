/**
 * Created on: 2002-11-13 16:06:30
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.lwap.ui.web.Form;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.IterationHandle;
import uncertain.composite.TextParser;

/**
 * 
 * 
 *   parseParameter()
 *   createModel()
 * 	 createView()
 * 
 * On FormPost:
 *   doService(){
 *   	* onValidateInput()
 *   	if( no_error)
 *      	* onFormPost()
 *   	else
 *      	setViewOutput(true)
 * 	 }
 * * designate an overridable method 
 * 
 * Override this method to get Form object from view:
 *   createForm()
 */

public class FormBasedService extends PolyStateService {
	
	public static final String KEY_SUCCESS_PROMPT = "success-prompt";
	public static final String KEY_VALIDATION_ACTION = "validation-action";
	public static final String KEY_VALIDATION_RULE   = "validation-rule";
	public static final String KEY_TEST			   = "Test";
	public static final String KEY_FORM_FIELD	   = "FormField";	
	public static final String KEY_PROMPT	       = "Prompt";	
	public static final String KEY_SUCCESS_VALUE	   = "SuccessValue";
	public static final String KEY_FAIL_VALUE	   = "FailValue";		
	
	public class FormFinder implements IterationHandle {
		
		public CompositeMap form;
		
		public int process( CompositeMap map){
			String name = map.getName();
			if( name != null)
				if( name.equals("form")){
					form = map;
					return IterationHandle.IT_BREAK;
				}
			return IterationHandle.IT_CONTINUE;	
		}
	};
	
	protected Form the_form;
	boolean   param_valid = false;
	
	public static final String STATE_FORM_POST = "FormPost";	
	public static final String STATE_FORM_SHOW = "FormShow";
	public static final String KEY_FORM = "Form";
	public static final String KEY_PARAM_PASSON = "PassOn";
		

	public void detectServiceState()
		throws InvalidInvokeException,IOException
	{
		if( request.getMethod().equalsIgnoreCase("POST"))
			setState(STATE_FORM_POST);
		else
			setState(STATE_FORM_SHOW);
	}
	
	public boolean isParameterValid(){
		return param_valid;
	}
	
	
	public void setParameterValid(boolean v){
		param_valid = v;
	}
	
	public boolean isFormPost(){
		return getState().equals( STATE_FORM_POST );
	}

	
	/** create a org.lwap.ui.web.Form object from view config */
	protected Form createForm() throws ServletException {
		CompositeMap form_config = getFormConfig();
		if( form_config == null) throw new ServletException("Service configuration error: This service is assumed to be a FormBasedService, but no form found in view config.");
		try{				
			the_form = (Form)DynamicObject.cast(form_config, Form.class);
			the_form.setService(this);
			// pass defined parameters to form
			CompositeMap params = getParameterConfig();
			if( params != null){
				Iterator it = params.getChildIterator();
				if( it != null)
					while( it.hasNext()){
						CompositeMap param = (CompositeMap)it.next();
						Boolean b = param.getBoolean(KEY_PARAM_PASSON);
						if( b != null)
							if( b.booleanValue()){
								 //param.put(DataBindingConvention.KEY_DATAFIELD, "/parameter/@" + param.getString("Name"));
								 the_form.addParameter(param);
							}
					}
			}
			return the_form;			
			
		} catch(Throwable thr){
			//thr.printStackTrace();
			throw new ServletException(thr);
		}
	}
	
	/** gets form part in view config */
	public CompositeMap getFormConfig(){
		CompositeMap view_config = getViewConfig();
		if(view_config == null) return null;
		
/*
		String path = getServiceConfig().getString(KEY_FORM, "form");
		Object obj = view_config.getObject(path);
		return obj instanceof CompositeMap? (CompositeMap)obj: null;
*/		

        FormFinder f = new FormFinder();
        view_config.iterate(f,true);
        return f.form;
	}
	
	public Form getForm(){
		return the_form;
	}
	
	/** override default method to instantiate the Form object */

	public void createView() throws IOException, ServletException {
		if( the_form == null){ 
			createForm();
		}
		super.createView();
		
	}


	public void populateView() throws IOException, ServletException {
		super.populateView();
		the_form.initForm();		
	}


	/** called on form show, do nothing*/
	public void doServiceFormShow() throws  IOException, ServletException {
		
	}
	
	public boolean performValidation(CompositeMap model, CompositeMap validation_rule)
	throws ServletException
	{
		boolean success = true;
		Iterator it = validation_rule.getChildIterator();
		if( it == null) return true;
		while( it.hasNext()){
			CompositeMap rule = (CompositeMap)it.next();

			String test_field = rule.getString(KEY_TEST);
			if( test_field == null) throw new ServletException("FormBasedService:Must specify 'Test' for validation rule");

			String fail_value = rule.getString(KEY_FAIL_VALUE);
			String success_value = rule.getString(KEY_SUCCESS_VALUE);
			String form_field = rule.getString(KEY_FORM_FIELD);
			String prompt     = getLocalizedString(rule.getString(KEY_PROMPT));
			prompt = TextParser.parse(prompt, model);

			String str_value = "";
			Object value = model.getObject(test_field);
			if( value != null) str_value = value.toString();
			
			if( fail_value != null){
				if( fail_value.equals(str_value)){
					success = false;
					the_form.setErrorPrompt(form_field, prompt);
				}
			} else if( success_value != null){
				if( !success_value.equals(str_value)){
					success = false;
					the_form.setErrorPrompt(form_field, prompt);
				}

			} else
				throw new ServletException("FormBasedService:Must specify either 'SuccessValue' of 'FailValue' for validation rule");
		}
		return success;
	}
	
	public void onValidateInput( CompositeMap parameters) throws ValidationException, ServletException {
		CompositeMap va = getServiceConfig().getChild(KEY_VALIDATION_ACTION);
		if( va != null ){
			super.databaseAccess(va,getParameters(), getModel());
		}
		CompositeMap vr = getServiceConfig().getChild(KEY_VALIDATION_RULE);
		if( vr != null) 
			if(! performValidation(getModel(), vr)) 
				throw new ValidationException();
	}
	
	public void onFormPost() throws IOException, ServletException {
		CompositeMap action_config = super.getActionConfig();
		if(action_config != null)
			super.databaseAccess(action_config,getParameters(),getModel());
	}
	

	public void doServiceFormPost() throws IOException, ServletException {
		
		param_valid = the_form.parseParameter( super.getParameterSource(), getParameters());
		if(param_valid){
			try{
				super.setViewOutput(false);
				onValidateInput( getParameters());
				onFormPost();

				// create success message
				/*
				String sm = this.getServiceConfig().getString(KEY_SUCCESS_PROMPT);
				if( sm != null){
					CompositeMap msg = getViewBuilderStore().createView("message");
					msg.putString("Prompt", sm );
					super.setViewOutput(true);
				}
				*/
				
			} catch(ValidationException ex){
				the_form.setError(ex);
				the_form.populateFormWithInput(super.getParameterSource());
				super.setViewOutput(true);				
			}
		} else{
			the_form.createFormMessage( getLocalizedString("prompt.form.re-input"), true );
			the_form.populateFormWithInput(super.getParameterSource());
			super.setViewOutput(true);
		}
		
	}
	

	
}

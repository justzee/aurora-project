/*
 * Created on 2005-10-10
 */
package org.lwap.controller;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.lwap.application.IExceptionFormater;
import org.lwap.ui.web.Form;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/**
 * FormController
 * @author Zhou Fan
 * 
 */
public class FormController extends AbstractController 
implements IFeature
{

    public static final String APP_ALLOW_FORM_REPEATED_SUBMIT = "allow-form-repeated-submit";
    public static final String KEY_REQUEST_COUNT = "__request_count__";
    public static final String KEY_INPUT_ERROR = "InputError";
    public static final String KEY_FORM_NAME = "_form_name";
    public static final String KEY_FORM_STATE = "_form_state";
    public static final String KEY_PARAMETER_VALID = "ParameterValid";
    public static final String FORM_POST = "formpost";
    public static final String KEY_COUNT_INCREASED = "__count_increased__";
    
    public static void setParameterValid(CompositeMap context, boolean valid){
        context.put(KEY_PARAMETER_VALID, new Boolean(valid));
    }
    
    // name of form
    String 			       form_name;

    CompositeMap	       formConfig;
    Form		  	       attachedForm;
    IExceptionFormater     exp_formater;
    boolean			       is_post = false;
    long                   mSubmitedCount = 0;
    Long                   mSessionCount; 
    Boolean                mAllowRepeatSubmit;
    boolean                mApplicationAllowResubmit = false;                 
    /**
     * @param engine
     */
    public FormController(UncertainEngine engine) {
        super(engine);
        exp_formater = (IExceptionFormater)engine.getObjectSpace().getParameterOfType(IExceptionFormater.class);
    }

    /* (non-Javadoc)
     * @see uncertain.proc.IFeature#onAttach(uncertain.composite.CompositeMap, uncertain.event.Configuration)
     */
    public int attachTo(CompositeMap config, Configuration procConfig) {
        formConfig = config;
        return IFeature.NORMAL;

    }
    
    void initRequestCount( HttpServletRequest request, CompositeMap context ){

        CompositeMap session = ServiceInstance.getSession();
        mSessionCount = (Long)session.get(KEY_REQUEST_COUNT);
        if( mSessionCount==null){
            mSessionCount = new Long( getRequestCount(ServiceInstance));
        }

        String value = request.getParameter(KEY_REQUEST_COUNT);
        if( value==null || "".equals(value)){
            mSubmitedCount = mSessionCount.longValue();
        }else{
            try{
                mSubmitedCount = Long.parseLong(value);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }         
        
        if( mSessionCount.longValue()<mSubmitedCount)
            mSessionCount = new Long(mSubmitedCount);
        
        setRequestCount(ServiceInstance, mSessionCount.longValue());
           
        
    }

    /* (non-Javadoc)
     * @see org.lwap.controller.IController#detectAction(javax.servlet.http.HttpServletRequest, uncertain.composite.CompositeMap)
     */
    public int detectAction(HttpServletRequest request, CompositeMap context) {
        initRequestCount(request, context);
        form_name = formConfig.getString("Name", "MAIN_FORM"); 
        StateFlag flag = new StateFlag(request);
        if(form_name==null || form_name.equalsIgnoreCase(request.getParameter(KEY_FORM_NAME))){
            String form_state = request.getParameter(KEY_FORM_STATE);
            if(FORM_POST.equalsIgnoreCase(form_state)){
                is_post = true;
                setProcedureName(ControllerProcedures.FORM_POST);
                return IController.ACTION_DETECTED;
            }
        }
        return IController.ACTION_NOT_DETECTED;
    }
    

    
   
	/** create a org.lwap.ui.web.Form object from view config */
	protected void createForm()  {
        attachedForm = (Form)DynamicObject.cast(formConfig, Form.class);
		attachedForm.setService(ServiceInstance);
		// pass defined parameters to form
		CompositeMap params = ServiceInstance.getParameterConfig();
		if( params != null){
			Iterator it = params.getChildIterator();
			if( it != null)
				while( it.hasNext()){
					CompositeMap param = (CompositeMap)it.next();
					Boolean b = param.getBoolean("PassOn");
					if( b != null)
						if( b.booleanValue()){
							 attachedForm.addParameter(param);
						}
				}
		}		
	}
	
	public void onPrepareService() throws Exception{
	    createForm();
	    attachedForm.initForm();
	}
    
    public void checkForRepeatedSubmit(){
        if( isFormPost()){
            if(mAllowRepeatSubmit==null)
                mAllowRepeatSubmit = this.attachedForm.getAllowRepeatedSubmit();
            /*
            System.out.println("==================================================");
            System.out.println("this.mAllowRepeatSubmit:"+mAllowRepeatSubmit);
            System.out.println("this.mApplicationAllowResubmit:"+this.mApplicationAllowResubmit);
            System.out.println("this.isAllowRepeatSubmit():"+this.isAllowRepeatSubmit());
            System.out.println("session click count:"+this.mSessionCount);
            System.out.println("request click count:"+this.mSubmitedCount);
            */
            if( isAllowRepeatSubmit() ) 
                return;
            if (!"POST".equals(ServiceInstance.getRequest().getMethod()))
                return;
            if(mSubmitedCount<mSessionCount.longValue()){
                 throw new RepeatedSubmitError(ServiceInstance.getLocalizedString("validation.resubmit"));
            }
            //increase count
            CompositeMap context = ServiceInstance.getServiceContext();
            boolean inc = context.getBoolean(KEY_COUNT_INCREASED, false);
            if(!inc){
                setRequestCount(ServiceInstance, mSessionCount.longValue()+1);
                context.putBoolean(KEY_COUNT_INCREASED, true);
            }            
        }
    }
    
    public void postPrepareService(){
        mApplicationAllowResubmit = ServiceInstance.getApplicationConfig().getBoolean(APP_ALLOW_FORM_REPEATED_SUBMIT, true);
        createRequestCountParam();
    }
    
    public void preCheckInput(){
        checkForRepeatedSubmit();        
    }
    
    void createRequestCountParam(){
        CompositeMap param = new CompositeMap("param");
        param.put("Name", KEY_REQUEST_COUNT);
        param.put("dataField", "/session/@"+KEY_REQUEST_COUNT);
        attachedForm.addParameter(param);        
    }

    public void onCheckInput() throws ValidationException {
		if(!isFormPost()) return;
	    boolean param_valid = attachedForm.parseParameter( 
		        ServiceInstance.getParameterSource(), 
		        ServiceInstance.getParameters()
		        );
		ServiceInstance.getServiceContext().put(KEY_PARAMETER_VALID, new Boolean(param_valid));
        if(!param_valid)
            throw new ValidationException();
	}
	
	public static boolean isInputValid(CompositeMap context){
	    return context.getBoolean(KEY_PARAMETER_VALID, true);
	}
	
	public void onCreateFormError( ProcedureRunner runner ){
		if(!isFormPost()) return;
        populateFormError(runner);
        // To determine
        runner.getContext().put("output", new Boolean(true));
	}
	
	public Form getForm(){
	    return attachedForm;
	}
	
	public boolean isFormPost(){
	    return is_post;
	}
	/*
	public void postDoAction(ProcedureRunner runner)
    {
	    if(ServiceInstance.getServiceDispatch()==null  ){
	        attachedForm.populateFormWithInput(ServiceInstance.getParameterSource());
	        ServiceInstance.setNextProcedure(ControllerProcedures.GENERATE_UI);
	    }
	}
    */
    
    public void onPostDone(ProcedureRunner runner)
    {
        if(ServiceInstance.getServiceDispatch()==null  ){
            attachedForm.populateFormWithInput(ServiceInstance.getParameterSource());
            ServiceInstance.setNextProcedure(ControllerProcedures.GENERATE_UI);
        }
    }    
    
    public void populateFormError(ProcedureRunner runner){
        Throwable exception = runner.getException();
        String msg = null;
        if(exp_formater!=null && exception!=null){
            msg = exp_formater.getMessage(exception, ServiceInstance.getServiceContext());           
        }
        if(msg==null){
            if( exception==null || exception instanceof ValidationException)
                msg = "prompt.form.re-input";
            else
                msg = exception.getMessage();
        }
        msg = ServiceInstance.getLocalizedString(msg);
        msg = TextParser.parse(msg, ServiceInstance.getServiceContext());
        attachedForm.createFormMessage( msg, true );
        attachedForm.populateFormWithInput(ServiceInstance.getParameterSource());
        attachedForm.put(KEY_INPUT_ERROR, "true");
    }
    
    public static long getRequestCount( MainService service ){
        HttpSession session = service.getRequest().getSession();
        Long c = (Long)session.getAttribute(KEY_REQUEST_COUNT);
        if(c==null){
            c = new Long(1);
        }
        return c.longValue();
    }
    
    public static void setRequestCount( MainService service, long count ){
        HttpSession session = service.getRequest().getSession();
        session.setAttribute(KEY_REQUEST_COUNT, new Long(count));
        service.getSession().put(KEY_REQUEST_COUNT, new Long(count));
    }

    /**
     * @return the mAllowRepeatSubmit
     */
    public boolean isAllowRepeatSubmit() {
        if(mAllowRepeatSubmit!=null) return mAllowRepeatSubmit.booleanValue();
        else return mApplicationAllowResubmit;
    }

    /**
     * @param allowRepeatSubmit the mAllowRepeatSubmit to set
     */
    public void setAllowRepeatSubmit(boolean allowRepeatSubmit) {
        mAllowRepeatSubmit = new Boolean( allowRepeatSubmit);
    }
    
    /*
    IExceptionHandle
    public boolean handleException(ProcedureRunner runner, Throwable exception){
        if("DoAction".equals(runner.getCurrentEvent())){
            //exception.printStackTrace();
            ServiceInstance.getServiceContext().put(KEY_PARAMETER_VALID, new Boolean(false));
            if(exp_formater==null) return false;
            String msg = null;
            if(exp_formater!=null)
                msg = exp_formater.getMessage(exception, ServiceInstance.getServiceContext());            
            if(msg==null) 
                msg = exception.getMessage();
            else{
                msg = ServiceInstance.getLocalizedString(msg);
                msg = TextParser.parse(msg, ServiceInstance.getServiceContext());
            }
            //System.out.println("message:"+msg);
            attachedForm.setErrorPrompt(null, msg);
            // to move out
            attachedForm.put(KEY_INPUT_ERROR, "true");
            //runner.locateTo("CheckValidationResult");   
            return true;
        }else
            return false;
    }
    */
    

}

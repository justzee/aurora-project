/**
 * Created on: 2003-4-2 17:44:05
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Iterator;

import org.lwap.application.BaseService;
import org.lwap.application.ServiceDispatch;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;

/**
 * <std:service Name="name_of_service" NewContext="true|false" >
 *    <parameter-transform>
 *       <param Name="parameterA" dataField="/model/paramA" />
 *       <param Name="parameterB" dataField="/model/paramB" />
 *    </parameter-transform>
 * </std:service>
 * 
 */
public class ServiceDispatchView implements View {

	public static final String KEY_PARAMETER_TRANSFORM = "parameter-transform";
	public static final String KEY_NEW_CONTEXT		 = "NewContext";	

	void transformParameter(CompositeMap model, CompositeMap view, CompositeMap context){
		
		CompositeMap param_transform = view.getChild(KEY_PARAMETER_TRANSFORM);
		if( param_transform == null) return;
		if( param_transform.getChilds() == null) return;
		
		CompositeMap params = context.getChild(BaseService.KEY_PARAMETER);
		if( params == null) params = context.createChild(BaseService.KEY_PARAMETER);
		
		for( Iterator it = param_transform.getChildIterator(); it.hasNext(); ){
			CompositeMap item = (CompositeMap)it.next();
			String name = item.getString("Name");
			String data_field = item.getString("dataField");
			if( name == null || data_field==null) continue;
			Object obj = model.getObject(data_field);
			if( obj != null){
				 params.put(name,obj);						
			}
		}

		
	}

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {

		model = DataBindingConvention.getDataModel(model,view);

			
		BaseService service = session.getService();
		WebApplication app  = (WebApplication)service.getApplication();
		
		String service_name = DataBindingConvention.parseAttribute("Name", model, view);
		if( service_name == null) throw new ViewCreationException("No 'Name' attribute in view config");

/*
		boolean new_context = view.getBoolean(KEY_NEW_CONTEXT, true);
		
		CompositeMap context;
		if( new_context)
			context = new CompositeMap();
		else
			context = service.getServiceContext();
*/
		CompositeMap context = new CompositeMap(null,null,"context");

		CompositeMap old_context =  service.getServiceContext();
		context.addChild(old_context.getChild(BaseService.KEY_SESSION));
//		context.addChild(old_context.getChild(BaseService.KEY_MODEL));
		
		
		ServiceDispatch disp = app.createServiceDispatch(service,service_name,ServiceDispatch.DISPATCH_STYLE_INCLUDE);
		if( disp == null)  throw new ViewCreationException("Can't create service dispatch:" + service_name);
		transformParameter(model, view, context);
//		System.out.println("std:service\r\n"+context.toXML());
		disp.setServiceContext(context);
		
		try{
		    disp.dispatch();
		} catch( Throwable thr){
			throw new ViewCreationException(thr);
		}
		
/*		
		app.createServiceDispatch()
		app.getService(service_name, service.getServiceContext());
*/		
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "service";
	}

}

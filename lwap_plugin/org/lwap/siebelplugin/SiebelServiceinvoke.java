/** Perform a Siebel Service call
 *  Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import com.siebel.data.SiebelService;

public class SiebelServiceinvoke extends AbstractEntry {

	SiebelInstance siebelInstance;

	public Parameter[] Parameters;
	public String Service_name;
	public String Method_name;
	public String Return_target;

	ILogger mLogger;
//	Logger mLogger;
	ILogger mErrorLogger;
	
	public SiebelServiceinvoke(SiebelInstance si) {
		siebelInstance = si;
	}

	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "siebel-serviceinvoke");
		invoke.put("service_name", Service_name);
		invoke.put("method_name", Method_name);
		invoke.put("return_target", Return_target);
		if (Parameters != null) {
			CompositeMap params = invoke.createChild("parameters");
			for (int i = 0; i < Parameters.length; i++)
				params.addChild(Parameters[i].toCompositeMap());
		}
		return invoke.toXML();
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.INFO, "siebel-serviceinvoke");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());

		MainService service = MainService.getServiceInstance(context.getRoot());
		
		CompositeMap target = null;
		CompositeMap model = null;
		if (service != null)
			model = service.getModel();
		else
			model = context.getRoot().getChild("model");
		if (model == null)
			model = context.getRoot().createChild("model");
		if (Return_target != null) {
			String t = TextParser.parse(Return_target, context);
			target = (CompositeMap) model.getObject(t);
			if (target == null)
				target = model.createChildByTag(t);
		}
		CompositeMap params =service.getParameters();
		CompositeMap siebel_login = new CompositeMap();
		
		service.databaseAccess("Sieble_Login.data", params, siebel_login);
		
		String USER_NAME = (String)siebel_login.getObject("siebel-login/@USER_NAME");
		String PWD = (String)siebel_login.getObject("siebel-login/@PWD");
		
		SiebelDataBean siebelDataBean = siebelInstance.getClient(USER_NAME,PWD);
//		SiebelDataBean siebelDataBean = siebelInstance.getClient();
		SiebelService siebelService = null;
		try {

			siebelService = siebelDataBean.getService(Service_name);
			mLogger.log(Level.CONFIG, "service Name:" + Service_name);

			if (siebelService == null) {
				throw new IllegalArgumentException("Service '" + Service_name
						+ "' not found in Siebel system.");
			}
				SiebelPropertySet input = new SiebelPropertySet();
				SiebelPropertySet output = new SiebelPropertySet();

				if (Parameters != null)
					for (int i = 0; i < Parameters.length; i++) {
						Parameter param = Parameters[i];

						handleInputParameter(param, input, context);
					}


				mLogger.log(Level.CONFIG, "call method " + Method_name);

				mLogger.log(Level.FINE, "**********input property*******");
				
				getProchild(0,0,input);
				
				siebelService.invokeMethod(Method_name, input, output);
				
				mLogger.log(Level.FINE, "**********output property*******");
				
				getProchild(0,0,output);
				
				if (Parameters != null)
					for (int i = 0; i < Parameters.length; i++) {
						Parameter param = Parameters[i];
						String t = TextParser.parse(param.Target, context);
						handleOutputParameter(param, output,
									target);
					}
				mLogger.log(Level.FINE,"target:"+target.toXML());
				// finish
					mLogger.log(Level.CONFIG,"Siebel service invoke finished");

			
		} catch (SiebelException ex) {
			ex.printStackTrace();
			throw new Exception("error when Siebel Service invoke:" + ex.getDetailedMessage(),
					ex);
		} finally {
			siebelService.release();
			mLogger.log(Level.FINE, "Siebel instance is released.");
			siebelInstance.release(USER_NAME);
		}

	}

	private void getProchild(int childIndex,int levle,SiebelPropertySet output) {
		getProperty(childIndex,levle,output);
		for(int i=0;i<output.getChildCount();i++){
			SiebelPropertySet sps = output.getChild(i);
			getProchild(i,levle+1,sps);
			
		}
	}

	private String getProperty(int childIndex,int level,SiebelPropertySet output) {
		String property = output.getFirstProperty();
		for(int i= 0;i<output.getPropertyCount();i++){
			String value = output.getProperty(property);
			mLogger.log(Level.FINE,childIndex+" "+getPre(level)+" property:"+property+" value "+value);
			property = output.getNextProperty();
		}
		return property;
	}
	public String getPre(int level){
		String pre_str = "";
		for(int i=0;i<level;i++){
			pre_str +=level;
		}
		return pre_str;
	}
	
	public void handleInputParameter(Parameter param, SiebelPropertySet input,
			CompositeMap context) {
		CompositeMap this_context = context;
		if (param.Datatype != null && param.Datatype.equals("hierarchy")
				&& param.Import != null
				&& param.Import.toLowerCase().equals("true")) {
			if (param.Source != null) {
				this_context = (CompositeMap) context.getObject(param.Source);
				List list = this_context.getChilds();
				for (int j = 0; j < list.size(); j++) {
					CompositeMap cm = (CompositeMap) list.get(j);
					iteratorInputParameters(param, input, cm);
				}
			}
			else{
				iteratorInputParameters(param, input, this_context);
			}
			return;
		}
		if (param.Import != null
				&& param.Import.toLowerCase().equals("true")) {
			Object o = param.Source_field == null ? param.Value : this_context
					.getObject(param.Source_field);
			String value = o == null ? "" : o.toString();
			input.setProperty(param.Name,value);
			mLogger.log(Level.CONFIG,"parameter " + param.Name + " -> " + value);
		}
	}
	
	

	private void iteratorInputParameters(Parameter param, SiebelPropertySet input,
			CompositeMap cm) {
		Parameter[] childParams = param.Parameters;
		SiebelPropertySet childSet = new SiebelPropertySet();
		for (int i = 0; i < childParams.length; i++) {
			Parameter childParam = childParams[i];
			handleInputParameter(childParam, childSet, cm);
		}
		input.addChild(childSet);
	}
	public void handleOutputParameter(Parameter param, SiebelPropertySet output,
			CompositeMap context) {

		CompositeMap this_context = context;
		if (param.Datatype != null && param.Datatype.equals("hierarchy")
				&& param.Export != null
				&& param.Export.toLowerCase().equals("true")) {
			for (int j = 0; j < output.getChildCount(); j++) {
				SiebelPropertySet this_output = output.getChild(j);
				if (param.Target != null) {
					this_context = new CompositeMap(param.Target);
					context.addChild(this_context);
				}
				iteratorOutputParameters(param, this_output, this_context);
			}
			return;
		}
		if (param.Export != null
				&& param.Export.toLowerCase().equals("true")) {
			String o = param.Value == null ? output.getProperty(param.Name):param.Value;
			String value = o == null ? "" : o.toString();
			context.put(param.Return_field, value);
				mLogger.log(Level.CONFIG,"parameter " + param.Return_field + " -> " + value);
		}
	}
	private void iteratorOutputParameters(Parameter param, SiebelPropertySet output,
			CompositeMap cm) {
		Parameter[] childParams = param.Parameters;
		for (int i = 0; i < childParams.length; i++) {
			Parameter childParam = childParams[i];
			handleOutputParameter(childParam, output, cm);
		}
	}
	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
//		mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
		mErrorLogger = LoggingContext.getErrorLogger(m);
	}

}

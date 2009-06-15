/** query Siebel BusComp
 *  Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.io.File;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import jcifs.smb.SmbFile;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.lwap.controller.ControllerProcedures;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;

public class InvokeMethod implements IFeature, IController  {

	SiebelInstance siebelInstance;

	public String Bo_name;
	public String Bc_name;
	public String Invoke_method;
	public String Parameters;
	
	public ParentBc[] Parentbcs;
	public FieldMapping[] Fieldmappings;
	
	public String Return_flag;
	public int Viewmode = -1;
	//delete file
	public String Destdir;
	
	MainService service;
//	public String InputFilePath;
	

//	Logger mLogger;
	ILogger mLogger;
	ILogger mErrorLogger;

	public InvokeMethod(SiebelInstance si) {
		siebelInstance = si;
	}
	public void addParentbcs(CompositeMap cm){
		List childs = cm.getChilds();
		Parentbcs = new ParentBc[childs.size()];
		Iterator ite = childs.iterator();
		int i=0;
		while(ite.hasNext()){
			CompositeMap child = (CompositeMap)ite.next();
			Parentbcs[i] = createParentBc(child);
			i++;
		}
	}
	public void addfieldmappings(CompositeMap cm){
		List childs = cm.getChilds();
		Fieldmappings = new FieldMapping[childs.size()];
		Iterator ite = childs.iterator();
		int i=0;
		while(ite.hasNext()){
			CompositeMap child = (CompositeMap)ite.next();
			Fieldmappings[i] = createFieldMapping(child);
			i++;
		}
	}
	private ParentBc createParentBc(CompositeMap cm){
		ParentBc pb = new ParentBc();
		int viewmode = -1;
		String bc_name = cm.getString("bc_name");
		Integer o_viewmode = cm.getInt("viewmode");
		if(o_viewmode != null)
			viewmode = o_viewmode.intValue();
		
		List parameterss = cm.getChilds();
		Iterator pss_ite = parameterss.iterator();
		if(pss_ite.hasNext()){
			CompositeMap c_parameters = (CompositeMap)pss_ite.next();
			
			List parameters = c_parameters.getChilds();
			Iterator ps_ite = parameters.iterator();
			Parameter[] Parameters = new Parameter[parameters.size()];
			
			int i= 0;
			while(ps_ite.hasNext()){
				CompositeMap child = (CompositeMap)ps_ite.next();
				Parameter p = createParameter(child);
				Parameters[i] = p;
				i++;
			}
			pb.Bc_name = bc_name;
			pb.Viewmode = viewmode;
			pb.Parameters = Parameters;
			return pb;
		}
		return pb;
		
	}
	private FieldMapping createFieldMapping(CompositeMap cm){
		FieldMapping fieldMapping = new FieldMapping();
		
	     String   Name = cm.getString("name");
	     String   Source_field = null;
	     String   Value = null;
	     Object   o_Source_field = cm.getString("source_name");
	     if(o_Source_field != null){
	    	 Source_field = o_Source_field.toString();
	     }
	     Object   o_Value = cm.getString("value");
	     
	     if(o_Value != null){
	    	 Value = o_Value.toString();
	     }
	     fieldMapping.Name = Name;
	     fieldMapping.Source_name = Source_field;
	     fieldMapping.Value = Value;
	     
		return fieldMapping;
	}
	private Parameter createParameter(CompositeMap cm){
		Parameter parameter = new Parameter();
		
	     String   Name = cm.getString("name").toString();
	     String   Source_field = null;
	     String   Value = null;
	     Object   o_Source_field = cm.getString("source_field");
	     if(o_Source_field != null){
	    	 Source_field = o_Source_field.toString();
	     }
	     Object   o_Value = cm.getString("value");
	     
	     if(o_Value != null){
	    	 Value = o_Value.toString();
	     }
	     parameter.Name = Name;
	     parameter.Source_field = Source_field;
	     parameter.Value = Value;
	     
		return parameter;
	}
	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "invoke-method");
		invoke.put("bo_name", Bo_name);
		invoke.put("bc_name", Bc_name);
		invoke.put("invoke_method", Invoke_method);
		invoke.put("paramters", Parameters);
		invoke.put("viewmode", Integer.toString(Viewmode));
//		invoke.put("InputFilePath", InputFilePath);
		if (Parentbcs != null) {
			CompositeMap bc = invoke.createChild("parentbcs");
			for (int i = 0; i < Parentbcs.length; i++)
				bc.addChild(Parentbcs[i].toCompositeMap());
		}

		if (Fieldmappings != null) {
			CompositeMap bc = invoke.createChild("field-mappings");
			for (int i = 0; i < Fieldmappings.length; i++)
				bc.addChild(Fieldmappings[i].toCompositeMap());
		}
		return invoke.toXML();
	}

	public void postPostDone(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.FINE, context.toXML());
		mLogger.log(Level.INFO, "invoke-method");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());


		MainService service = MainService.getServiceInstance(context.getRoot());
		CompositeMap target = null;
		CompositeMap model = null;
		
		Return_flag = "N";
		
		if (service != null)
			model = service.getModel();
		else
			model = context.getRoot().getChild("model");
		if (model == null)
			model = context.getRoot().createChild("model");
		if (Bo_name == null)
			throw new ConfigurationError(
					"Must set 'bo_name' attribute for invoke_method");
		if (Bc_name == null)
			throw new ConfigurationError(
					"Must set 'bc_name' attribute for invoke_method");
//		if (InputFilePath == null)
//			throw new ConfigurationError(
//					"Must set 'inputfilepath' attribute for invoke_method");
		if (Invoke_method == null)
			throw new ConfigurationError(
					"Must set 'invoke_method' attribute for invoke_method");	
		if (Return_flag != null) {
			String t = TextParser.parse(Return_flag, context);
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
		SiebelBusObject busObject = null;
		SiebelBusComp busComp = null;
		try {
			
			
			busObject = siebelDataBean.getBusObject(Bo_name);
			mLogger.log(Level.CONFIG, "busObject : " + Bo_name);

			if (busObject == null) {
				throw new IllegalArgumentException("busObject '" + Bo_name
						+ "' not found in Siebel system.");
			}
			busComp = busObject.getBusComp(Bc_name);
			mLogger.log(Level.CONFIG, "busComp : " + Bc_name);

			if (busComp == null) {
				throw new IllegalArgumentException("busComp '" + Bc_name
						+ "' not found in busObject " + Bo_name + ".");
			}
			if (Viewmode != -1) {
				busComp.setViewMode(Viewmode);
				mLogger.log(Level.CONFIG, "viewmode : " + Viewmode);
			}
			
			String file_name = context.getObject("/parameter/@UPLOAD_FILE").toString();
			
			handle(busObject,busComp,context);
			
			Return_flag = "Y";
			target.put(Return_flag, Return_flag);
			//delete tmp file
			String smbFileName=Destdir+File.separator+file_name;
			mLogger.log(Level.INFO, "delete file :"+smbFileName);
			SmbFile rmifile =  new SmbFile(smbFileName);
			rmifile.delete();
			
			mLogger.log(Level.INFO, "Invoke_method invoke finished");

		} catch (SiebelException ex) {
			throw new Exception("error when invoke method buscomp:"
					+ ex.getDetailedMessage(), ex);
		} finally {
			busComp.release();
			busObject.release();
			mLogger.log(Level.FINE, "Siebel instance is released.");
			siebelInstance.release(USER_NAME);

		}

	}

	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
//		mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
		mErrorLogger = LoggingContext.getErrorLogger(m);
	}

	public int attachTo(CompositeMap arg0, Configuration arg1) {
		return IFeature.NORMAL;
	}

	public int detectAction(HttpServletRequest arg0, CompositeMap arg1) {
		return IController.ACTION_DETECTED;
	}

	public String getProcedureName() {
		return ControllerProcedures.FORM_POST;
	}

	public void setServiceInstance(MainService service_inst) {
		service = service_inst;
	}
	public void getParentBc(SiebelBusObject busObject, CompositeMap source) {
		boolean hasRecord = true;
		SiebelBusComp busComp = null;

		if (Parentbcs != null) {
			for (int i = 0; i < Parentbcs.length && hasRecord; i++) {
				ParentBc pbc = Parentbcs[i];
				try {
					mLogger.log(Level.CONFIG,"parent Bc_name "+Bc_name);
					busComp = busObject.getBusComp(pbc.Bc_name);
				} catch (SiebelException e) {
					throw new IllegalArgumentException("busComp '"
							+ pbc.Bc_name + "' not found in busObject "
							+ Bo_name + ".");
				}

					try {
						if (pbc.Viewmode != -1)
							busComp.setViewMode(Viewmode);
						busComp.clearToQuery();
						if (pbc.Parameters != null)
							for (int j = 0; j < pbc.Parameters.length; j++) {
								Parameter param = pbc.Parameters[j];
								if (param.Import != null
										&& param.Import.toLowerCase().equals("true")) {
									Object o = param.Source_field == null ? param.Value
											: source
													.getObject(param.Source_field);
									String value = o == null ? "" : o
											.toString();
									if (!value.equals(""))
										busComp
												.setSearchSpec(param.Name,
														value);

									mLogger.log(Level.CONFIG,"parameter " + param.Name
												+ " -> " + value);

								}
							}
						busComp.executeQuery2(true, true);
						hasRecord = busComp.firstRecord();
					} catch (SiebelException e) {
						throw new IllegalArgumentException(e.getErrorMessage());
					}

			}
		}

	}
	public void handle(SiebelBusObject busObject,SiebelBusComp busComp,CompositeMap cm){
		getParentBc(busObject, cm);
		try {
			
			String[] parameter =  null;
			String file_name = cm.getObject("/parameter/@UPLOAD_FILE").toString();
			
			if(Parameters != null){
				mLogger.log(Level.CONFIG, "Paramters : " + Parameters);
				parameter = Parameters.split(",");
				parameter[0] = parameter[0]+File.separator+file_name;
			}
			
			mLogger.log(Level.FINE, "newRecord");
			busComp.newRecord(true);

			for (int k = 0; k < Fieldmappings.length; k++) {
				FieldMapping m = Fieldmappings[k];
				Object o = m.Source_name == null ? m.Value : cm
						.getObject(m.Source_name);
				String value = o == null ? "" : o.toString();
				mLogger.log(Level.CONFIG,"set "+m.Name+" -> "+value);
				busComp.setFieldValue(m.Name, value);

			}
			mLogger.log(Level.FINE, "invoke method: "+Invoke_method);
			busComp.invokeMethod(Invoke_method, parameter);
			
			mLogger.log(Level.FINE, "save record ");
			busComp.writeRecord();
		} catch (SiebelException e) {
			mLogger.log(Level.CONFIG,e.getDetailedMessage());
			throw new IllegalArgumentException(e.getErrorMessage());
		}
		try {
			busComp.clearToQuery();
		} catch (SiebelException e) {
			mLogger.log(Level.CONFIG,e.getDetailedMessage());
			throw new IllegalArgumentException(e.getErrorMessage());
		}
	}

}

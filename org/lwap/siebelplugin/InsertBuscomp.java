/** insert Siebel BusComp in  step
 *  Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.ParameterList;
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelService;

public class InsertBuscomp extends AbstractEntry {

	SiebelInstance siebelInstance;

	public String Bo_name;
	public String Bc_name;
	public int Viewmode = -1;
	public Selected Selected;
	
	public ParentBc[] Parentbcs;
	public FieldMapping[] Fieldmappings;

	public String Source;

	ILogger mLogger;
//	Logger mLogger;
//	ILogger mErrorLogger;

	public InsertBuscomp(SiebelInstance si) {
		siebelInstance = si;
	}

	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "insert-buscomp");
		
		invoke.put("bo_name", Bo_name);
		invoke.put("bc_name", Bc_name);
		invoke.put("source", Source);

		if (Parentbcs != null) {
			CompositeMap bc = invoke.createChild("parentbcs");
			for (int i = 0; i < Parentbcs.length; i++)
				bc.addChild(Parentbcs[i].toCompositeMap());
		}

		if (Fieldmappings != null) {
			CompositeMap bc = invoke.createChild("field_mappings");
			for (int i = 0; i < Fieldmappings.length; i++)
				bc.addChild(Fieldmappings[i].toCompositeMap());
		}
		return invoke.toXML();
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.INFO, "insert-buscomp");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());
		
		MainService service = MainService.getServiceInstance(context.getRoot());
		CompositeMap target = null;
		if (Source != null) {
			String t = TextParser.parse(Source, context);
			target = (CompositeMap) context.getObject(t);
		}
		mLogger.log(Level.FINE, "context "+context.toXML());

		CompositeMap params =service.getParameters();
		CompositeMap siebel_login = new CompositeMap();
		
		service.databaseAccess("Sieble_Login.data", params, siebel_login);
		
		String USER_NAME = (String)siebel_login.getObject("siebel-login/@USER_NAME");
		String PWD = (String)siebel_login.getObject("siebel-login/@PWD");
		
		long time = System.currentTimeMillis();
		SiebelDataBean siebelDataBean = siebelInstance.getClient(USER_NAME,PWD,time);
//		SiebelDataBean siebelDataBean = siebelInstance.getClient();
		SiebelBusObject busObject = null;
		SiebelBusComp busComp = null;
		try {

			busObject = siebelDataBean.getBusObject(Bo_name);
			mLogger.log(Level.CONFIG, "busObject :" + Bo_name);

			if (busObject == null) {

				throw new IllegalArgumentException("busObject '" + Bo_name
						+ "' not found in Siebel system.");
			}
			
				busComp = busObject.getBusComp(Bc_name);
				mLogger.log(Level.CONFIG, "busComp :" + Bc_name);

				if (busComp == null) {

				throw new IllegalArgumentException("busComp '" + Bc_name
						+ "' not found in busObject " + Bo_name + ".");

				}
					if (Viewmode != -1){
						busComp.setViewMode(Viewmode);
						mLogger.log(Level.CONFIG, "viewmode :" + Viewmode);

					}

					if(Source != null){
						List list = target.getChilds();
						for (int i = 0; i < list.size(); i++) {
							busComp.clearToQuery();
							CompositeMap cm = (CompositeMap) list.get(i);
							handle(busObject,busComp,cm);
						}
					}else
						handle(busObject,busComp,context);
					// finish
					mLogger.log(Level.INFO, "insertBusComp invoke finished");
				
			
		} catch (SiebelException ex) {
			ex.printStackTrace();
			throw new Exception("error when insert Siebel busComp:"
					+ ex.getDetailedMessage(), ex);
		} finally {
			busComp.release();
			busObject.release();
			mLogger.log(Level.FINE, "Siebel instance is released.");
			siebelInstance.release(USER_NAME,time);

		}

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
			busComp.newRecord(true);

			for (int k = 0; k < Fieldmappings.length; k++) {
				FieldMapping m = Fieldmappings[k];
				Object o = m.Source_name == null ? m.Value : cm
						.getObject(m.Source_name);
				String value = o == null ? "" : o.toString();
				mLogger.log(Level.CONFIG,"set "+m.Name+" -> "+value);
				busComp.setFieldValue(m.Name, value);

			}
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
	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
//		mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
//		mErrorLogger = LoggingContext.getErrorLogger(m);
	}
}

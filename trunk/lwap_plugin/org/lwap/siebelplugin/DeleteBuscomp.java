/** delete Siebel BusComp
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

import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;

public class DeleteBuscomp extends AbstractEntry {

	SiebelInstance siebelInstance;

	public Parameter[] Parameters;
	public String Bc_name;
	public String Bo_name;
	public String Source;
	public Selected Selected;

	public int Viewmode = -1;

	ILogger mLogger;
//	Logger mLogger;
	ILogger mErrorLogger;

	public DeleteBuscomp(SiebelInstance si) {
		siebelInstance = si;
	}

	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "delete-buscomp");

		invoke.put("bo_name", Bo_name);
		invoke.put("bc_name", Bc_name);
		invoke.put("viewmode", Integer.toString(Viewmode));
		invoke.put("source", Source);

		if (Parameters != null) {
			CompositeMap params = invoke.createChild("parameters");
			for (int i = 0; i < Parameters.length; i++)
				params.addChild(Parameters[i].toCompositeMap());
		}
		return invoke.toXML();
	}

	public void addSelected(Selected Selecteds) {
		this.Selected = Selecteds;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.INFO, "delete-buscomp");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());

		CompositeMap target = null;
		if (Source != null) {
			String t = TextParser.parse(Source, context);
			target = (CompositeMap) context.getObject(t);
		}
		mLogger.log(Level.FINE, "context " + context.toXML());

		MainService service = MainService.getServiceInstance(context.getRoot());
		CompositeMap params =service.getParameters();
		CompositeMap siebel_login = new CompositeMap();
		
		service.databaseAccess("Sieble_Login.data", params, siebel_login);
		
		String USER_NAME = (String)siebel_login.getObject("siebel-login/@USER_NAME");
		String PWD = (String)siebel_login.getObject("siebel-login/@PWD");
		
		SiebelDataBean siebelDataBean = siebelInstance.getClient(USER_NAME,PWD);
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
			if (Viewmode != -1) {
				busComp.setViewMode(Viewmode);
				mLogger.log(Level.CONFIG, "viewmode :" + Viewmode);
			}
			if (busComp == null) {
				throw new IllegalArgumentException("busComp '" + Bc_name
						+ "' not found in busObject " + Bo_name + ".");
			}
			if (Source != null) {
				List list = target.getChilds();
				for (int i = 0; i < list.size(); i++) {
					busComp.clearToQuery();
					CompositeMap cm = (CompositeMap) list.get(i);
					handle(busComp, cm);
				}
			} else
				handle(busComp, context);
			// finish
			mLogger.log(Level.INFO, "deleteBusComp invoke finished");

		} catch (SiebelException ex) {
			ex.printStackTrace();
			throw new Exception("error when delete Siebel BusComp :"
					+ ex.getDetailedMessage(), ex);
		} finally {
			busComp.release();
			busObject.release();
			mLogger.log(Level.FINE, "Siebel instance is released.");
			siebelInstance.release(USER_NAME);
		}

	}

	public void handle(SiebelBusComp busComp, CompositeMap cm) throws Exception {
		int selected_value = 1;
		if (Selected != null) {
			Object o = Selected.Source_field == null ? Selected.Value : cm
					.getObject(Selected.Source_field);
			int value = o == null ? 1 : Integer.valueOf(o.toString())
					.intValue();
			mLogger.log(Level.CONFIG, "selected £º" + value);
			selected_value = value;
		}
		if (selected_value == 0)
			return;
		if (Parameters != null)
			for (int j = 0; j < Parameters.length; j++) {
				Parameter param = Parameters[j];
				if (param.Import != null
						&& param.Import.toLowerCase().equals("true")) {
					Object o = param.Source_field == null ? param.Value : cm
							.getObject(param.Source_field);
					String value = o == null ? "" : o.toString();
					if (!value.equals(""))
						try {
							busComp.setSearchSpec(param.Name, value);
						} catch (SiebelException ex) {

							throw new Exception(
									"error when delete siebel buscomp:"
											+ ex.getDetailedMessage(), ex);
						}
					mLogger.log(Level.CONFIG, "parameter " + param.Name
							+ " -> " + value);
				}
			}
		try {
			busComp.executeQuery2(false, true);
		} catch (SiebelException ex) {
			throw new Exception("error when delete siebel buscomp:"
					+ ex.getDetailedMessage(), ex);
		}
		boolean hasRecord = false;
		boolean notLastRecord = true;
		try {
			hasRecord = busComp.firstRecord();
		} catch (SiebelException e) {
			mLogger.log(Level.INFO, "queryBuscomp has no record");
		}
		while (hasRecord && notLastRecord) {
			try {
				notLastRecord = busComp.nextRecord();
			} catch (SiebelException e) {
				mLogger.log(Level.INFO, "this is last record" );
			}
			try {
				if (notLastRecord)
					busComp.previousRecord();
				busComp.deleteRecord();
				mLogger.log(Level.CONFIG, "delete successful");
			} catch (SiebelException ex) {
				mLogger.log(Level.CONFIG, ex.getDetailedMessage());
				throw new Exception("error when delete siebel buscomp:"
						+ ex.getDetailedMessage(), ex);
			}
		}

	}

	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
//		mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
		mErrorLogger = LoggingContext.getErrorLogger(m);
	}
}

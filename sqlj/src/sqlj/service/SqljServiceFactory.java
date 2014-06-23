package sqlj.service;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.http.*;

import sqlj.core.IProcedureFactory;
import sqlj.core.ProcedureFactory;
import uncertain.core.UncertainEngine;
import aurora.application.action.HttpSessionCopy;
import aurora.application.features.HttpRequestTransfer;

public class SqljServiceFactory {
	private UncertainEngine mEngine;
	private IProcedureFactory procFactory;

	public SqljServiceFactory(UncertainEngine engine) {
		super();
		this.mEngine = engine;
		procFactory = (IProcedureFactory) engine.getObjectRegistry()
				.getInstanceOfType(IProcedureFactory.class);
		//TODO
		if (procFactory == null)
			procFactory = new ProcedureFactory();
	}

	public SqljService createSqljServiceInstance(HttpServletRequest req,
			HttpServletResponse resp, HttpServlet servlet) throws IOException {
		SqljService svc = new SqljService("sqlj", mEngine.getProcedureManager());
		prepareProcedure(svc, req, resp);
		svc.setRequest(req);
		svc.setResponse(resp);
		svc.setServlet(servlet);
		svc.setObjectRegistry(mEngine.getObjectRegistry());
		
		HttpRequestTransfer.copyRequest(svc);
		HttpSessionCopy.copySession(svc.getContextMap(), req.getSession(false));
		return svc;
	}

	protected void prepareProcedure(SqljService context, HttpServletRequest req,
			HttpServletResponse resp) throws IOException{
		String pathInfo = req.getPathInfo();
		StringTokenizer st = new StringTokenizer(pathInfo, "/");
		String procName = null, methodName = null;
		if (st.hasMoreElements()) {
			procName = st.nextToken();
			if (st.hasMoreElements()) {
				methodName = st.nextToken();
				if (st.hasMoreElements()) {
					resp.sendError(500, "extra segments:" + st.nextToken());
					return;
				}
			} else {
				resp.sendError(500, "method not specified");
				return;
			}
		} else {
			resp.sendError(500, "procedure not specified");
			return;
		}

		context.setProcName(procName);
		context.setMethodName(methodName);
		context.setProcedureFactory(procFactory);
	}
}

package sqlj.service;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aurora.service.IService;
import aurora.service.http.AbstractFacadeServlet;
import aurora.service.http.HttpServiceFactory;

public class SqljExecuteServlet extends AbstractFacadeServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1751012318424936714L;
	private SqljServiceFactory svcFactory;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		svcFactory = (SqljServiceFactory) getUncertainEngine()
				.getObjectRegistry()
				.getInstanceOfType(SqljServiceFactory.class);
		if(svcFactory==null)
			svcFactory = new SqljServiceFactory(getUncertainEngine());
		if (svcFactory == null)
			throw new ServletException(
					"No ServiceFactory instance registered in UncertainEngine");
	}

	@Override
	protected IService createServiceInstance(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SqljService svc = svcFactory.createSqljServiceInstance(request,
				response, this);
		return svc;
	}

	@Override
	protected void populateService(HttpServletRequest request,
			HttpServletResponse response, IService service) throws Exception {

	}

	@Override
	protected void handleException(HttpServletRequest request,
			HttpServletResponse response, Throwable ex) throws IOException,
			ServletException {
		//TODO
		while(ex.getCause()!=null)
			ex=ex.getCause();
		response.sendError(500,ex.getMessage());
	}

	@Override
	protected void cleanUp(IService service) {
		((SqljService)service).clean();
	}

}

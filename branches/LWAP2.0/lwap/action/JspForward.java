package org.lwap.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IConfigurable;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

/**
 * @author bobbie.zou 2009-7-9
 *
 */
public class JspForward extends AbstractEntry implements IConfigurable {
	public static final String KEY_JSP_FORWARD = "jsp-forward";
	String tagName;
	String page;
	public void run(ProcedureRunner runner) throws Exception {
		// TODO Auto-generated method stub
		if(KEY_JSP_FORWARD.equalsIgnoreCase(tagName)){
			CompositeMap context = runner.getContext();
			MainService svc = MainService.getServiceInstance(context);
			HttpServletRequest request = svc.getRequest();
			HttpServletResponse response = svc.getResponse();
			page = TextParser.parse(page, context);		
			request.getRequestDispatcher(page).forward(request, response);
		}
	}
	
	public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

	public void beginConfigure(CompositeMap config) {
		// TODO Auto-generated method stub
		tagName=config.getName();		
	}

	public void endConfigure() {
		// TODO Auto-generated method stub
		
	}	
}

package org.lwap.action;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class AjaxRequestTag extends AbstractEntry {

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		MainService svc = MainService.getServiceInstance(context);
		String ajax = svc.getRequest().getHeader("x-requested-with");
		if("XMLHttpRequest".equals(ajax)){
			context.put("ajaxTag", new Integer(1));	
		}
	}

}

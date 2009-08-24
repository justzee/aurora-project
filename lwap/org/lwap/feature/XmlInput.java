/*
 * Created on 2007-6-14
 */
package org.lwap.feature;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.lwap.controller.MainService;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

/** Parse input stream in JSON format and put into parameter */

public class XmlInput {
    
    public void onParseParameter(ProcedureRunner runner)
        throws IOException, SAXException
    {
        CompositeMap context = runner.getContext();
        MainService svc = MainService.getServiceInstance(context);
        HttpServletRequest  request = svc.getRequest();
        InputStream is = request.getInputStream();
        CompositeLoader loader = new CompositeLoader();
        CompositeMap map = loader.loadFromStream(is);
        CompositeMap params = svc.getParameters();
        params.addChild(map);     
    }

}

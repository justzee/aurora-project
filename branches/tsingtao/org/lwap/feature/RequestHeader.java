/*
 * Created on 2008-11-29
 */
package org.lwap.feature;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;

public class RequestHeader {
    
    String  mNames;
    
    public static void addRequestIp( HttpServletRequest request, CompositeMap map ){
        String address = request.getRemoteAddr();
        map.put("address", address);
    }
    
    public static CompositeMap createHeaderMap( HttpServletRequest request, String[] names ){
        CompositeMap request_head = new CompositeMap( "request" );
        if(names==null){
            Enumeration e = request.getHeaderNames();
            while(e.hasMoreElements()){
                String name = (String)e.nextElement();
                String value = request.getHeader(name);
                request_head.put(name, value);
            }
        }else{
            for(int i=0; i<names.length; i++){
                request_head.put(names[i], request.getAttribute(names[i]));
            }
        }
        return request_head;
    }    

    /**
     * @return the mNames
     */
    public String getNames() {
        return mNames;
    }

    /**
     * @param names the mNames to set
     */
    public void setNames(String names) {
        mNames = names;
    }
    
    public void addRequestHeader( CompositeMap context ){
        MainService service = MainService.getServiceInstance(context);
        
    }
/*
 *         CompositeMap header = createHeaderMap(request);
        service_context.addChild(header);
        System.out.println(header.toXML());
 */    

}

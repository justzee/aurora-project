/*
 * Created on 2008-11-29
 */
package org.lwap.application.event;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * Encapsulates state flags in service context
 * @author Zhou Fan
 *
 */

public class SessionController extends DynamicObject {
    
    public static final String KEY_DISPATCH_URL = "__dispatch_url__";    
    public static final String KEY_GO_ON = "__service_go_on__";
    public static final String KEY_SESSION_VALID = "__session_valid__";
    
    public static SessionController createSessionController( CompositeMap context ){
        SessionController state = new SessionController();
        state.initialize(context);
        return state;
    }
    
    public boolean getContinueFlag(){
        return getBoolean(KEY_GO_ON, true);
    }
    
    public void setContinueFlag( boolean go_on ){
        putBoolean( KEY_GO_ON, go_on );
    }
    
    public String getDispatchUrl(){
        return getString(KEY_DISPATCH_URL);
    }
    
    public void setDispatchUrl( String url ){
        putString(KEY_DISPATCH_URL, url);
    }
    
    public boolean isSessionValid(){
        return getBoolean(KEY_SESSION_VALID, false);
    }
    
    public void setSessionValid( boolean valid ){
        putBoolean( KEY_SESSION_VALID, valid );
    }    

}

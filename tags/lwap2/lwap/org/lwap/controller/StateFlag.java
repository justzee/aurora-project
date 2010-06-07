/*
 * Created on 2005-10-10
 */
package org.lwap.controller;

import javax.servlet.ServletRequest;

/**
 * StateFlag
 * @author Zhou Fan
 * 
 */
public class StateFlag {

    public static final String KEY_ACTION_FLAG = "_action";
    public static final String KEY_OBJECT_FLAG = "_object";
    public static final String KEY_STATE_FLAG = "_state";  
    
    public static final String[] PRE_PARSED_PARAMETERS = {KEY_ACTION_FLAG, KEY_OBJECT_FLAG, KEY_STATE_FLAG};
    
    String	action;
    String  object;
    String  state;

    /**
     * construct from servlet request
     */
    public StateFlag(ServletRequest request) {
        action = request.getParameter(KEY_ACTION_FLAG);
        object = request.getParameter(KEY_OBJECT_FLAG);
    }
    
    

    /**
     * @param action The action to set.
     */
    public void setAction(String action) {
        this.action = action;
    }
    /**
     * @param object The object to set.
     */
    public void setObject(String object) {
        this.object = object;
    }
    /**
     * @return Returns the action.
     */
    public String getAction() {
        return action;
    }
    /**
     * @return Returns the object.
     */
    public String getObject() {
        return object;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
}

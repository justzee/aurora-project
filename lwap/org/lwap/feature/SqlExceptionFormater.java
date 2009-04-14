/*
 * Created on 2007-6-6
 */
package org.lwap.feature;

import java.sql.SQLException;

import org.lwap.application.IExceptionFormater;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;

public class SqlExceptionFormater implements IExceptionFormater,
        IGlobalInstance {
    
    public static final String KEY_CODE = "code";
    
    public static final String KEY_MESSAGE = "message";
    
    UncertainEngine     engine;
    CompositeMap        message_map;
    
    public String MapPath;
    public String Separator;
    
    
    public SqlExceptionFormater(UncertainEngine engine){
        this.engine = engine;
    }
    
    public void addMessage( CompositeMap message ){
        String code = message.getString(KEY_CODE);
        String msg = message.getString(KEY_MESSAGE);
        if( code!=null && msg!=null ){
            if(message_map==null)
                message_map = new CompositeMap("messages");
            message_map.put(code, msg);
        }
        else
            throw new ConfigurationError("Must set 'code' and 'message' property in sql message config:"+message.toXML());
    }
    
    public void onInitialize(){
        engine.getObjectSpace().registerInstance(IExceptionFormater.class, this);
        if(MapPath!=null){
            CompositeMap map = (CompositeMap)engine.getGlobalContext().getObject(MapPath);
            if( map == null) 
                throw new IllegalArgumentException("Can't get message map from path '"+MapPath+"'");
            if(message_map!=null)
                message_map.putAll(map);
            else
                message_map = map;
            
        }
    }
    
    public String getMessage(Throwable exception, CompositeMap context) {
        if(! (exception instanceof SQLException)){
            return null;
        }
        SQLException sex = (SQLException)exception;
        if(sex.getCause()!=null && sex.getCause() instanceof SQLException){
            sex = (SQLException)sex.getCause();
        }
        String error_msg = sex.getMessage();
        int code = sex.getErrorCode();
        if(message_map!=null){
            Object msg =  message_map.get(Integer.toString(code));
            if(msg!=null) error_msg = msg.toString(); 
        }
        if( Separator!=null){
            int id1 = error_msg.indexOf(Separator);
            int id2 = error_msg.lastIndexOf(Separator);
            if(id1>=0&&id2>=0)
                error_msg = error_msg.substring(id1+1, id2);
        }
        return error_msg;
    }

}

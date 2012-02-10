/*
 * Created on 2005-10-29
 */
package org.lwap.controller;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;

/**
 * ConfigCondition
 * @author Zhou Fan
 * 
 * <config-condition test="/parameter/@param" value="${expected value}">
 *    
 * </config-condition>
 * 
 */
public class ConfigCondition implements IFeature{
    
    public static final String KEY_DEFAULT_TEST_FIELD = "/parameter/@_state";
    
    public String Test;
    public String Value;
    public String State;
    
    CompositeMap	config;
    boolean meet_condition = false;
    
    public ConfigCondition() {

    }
    
    public boolean isMeetCondition(){
        return meet_condition;
    }
    
    public int attachTo(CompositeMap config, Configuration procConfig ){
        this.config = config;

        MainService service = MainService.getServiceInstance(config.getRoot());
        CompositeMap context = service.getServiceContext();
        
        String test_field = null;
        String test_value = null;
        if(Test != null && Value != null){
            test_field =  Test;
            test_value = Value;            
        }
        else if(State != null){
            test_field = KEY_DEFAULT_TEST_FIELD;
            test_value = State;
        }
        else throw new ConfigurationError("must either specify 'test','value' attribute or 'state' attribute");
        meet_condition = CompositeUtil.compareObject(context,test_field,test_value);
        //System.out.println("attached, meet:"+meet_condition+"\r\n"+config.toXML());
        if(meet_condition)
            return IFeature.NORMAL;
        else
            return IFeature.NO_CONFIG;
    }
    
    public void onPopulateServiceConfig(){
        CompositeMap config_parent = config.getParent();
        if(!meet_condition){
            //System.out.println(config.get("state")+" not meet condition");
            //config.getParent().getChilds().remove(config);
        }else{
            List config_list = config.getParent().getChilds();
            int idx = config_list.indexOf(config);
            config_list.remove(idx);
            List childs = config.getChilds();
            if(childs!=null)
                config_list.addAll(idx,childs);
            childs.clear();
        }        
        //System.out.println(config_parent.toXML());
    }


}

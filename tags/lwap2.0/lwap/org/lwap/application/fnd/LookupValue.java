/*
 * Created on 2005-10-27
 */
package org.lwap.application.fnd;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.proc.ProcedureRunner;

/**
 * LookupValue
 * @author Zhou Fan
 * 
 */
public class LookupValue {
    
    public String Id;
    public String Name;

    /**
     * 
     */
    public LookupValue() {

    }
    
    public void onCreateModel(ProcedureRunner runner) throws Exception {
        //System.out.println("lookupvalue invoked");
        if(Id==null) throw new ConfigurationError("must set 'id' property for lookup value list ");
        CompositeMap context = runner.getContext();
        MainService service = (MainService)context.get(MainService.KEY_SERVICE_INSTANCE);
        CompositeMap param = new CompositeMap("param");
        param.put("lookup_type_id", Id);
        service.databaseAccess("fnd_lookup_value.data",param,param);
        CompositeMap result = param.getChild("VALUE-LIST");
        if(result==null){
            return;
        }
        if(Name!=null) result.setName(Name);
        service.getModel().addChild(result);
    }

}

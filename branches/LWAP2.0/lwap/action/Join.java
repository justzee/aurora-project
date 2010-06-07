/*
 * Created on 2007-7-27
 */
package org.lwap.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import uncertain.util.StringSplitter;

public class Join extends AbstractEntry {
    
    public Join(){
    }
    
    public String   List1;
    public String   List2;
    public String   Keys;
    
    public void onValidateConfig(){
        if(List1==null) throw new ConfigurationError("[Join] Must specify 'source' attribute");
        if(List2==null) throw new ConfigurationError("[Join] Must specify 'target' attribute");
        if(Keys==null) throw new ConfigurationError("[Join] Must specify 'key' attribute");        
    }

    public void run(ProcedureRunner runner) throws Exception {
        onValidateConfig();
        CompositeMap context = runner.getContext();
        CompositeMap map1 = (CompositeMap)context.getObject(List1);
        CompositeMap map2 = (CompositeMap)context.getObject(List2);
        if(map1==null||map2==null) return;
        if(map1.getChilds()==null||map2.getChilds()==null) return;
        String[] key_array = StringSplitter.splitToArray(Keys, ',', false);
        CompositeUtil.join(map1.getChilds(), map2.getChilds(), key_array);
    }

}

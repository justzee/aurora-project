/*
 * Created on 2007-7-19
 */
package org.lwap.siebelplugin;

import uncertain.composite.CompositeMap;

public class FieldMapping {
    
    public String   Name;
    public String   Source_name;
    public String   Value;
    public CompositeMap toCompositeMap(){
        CompositeMap param = new CompositeMap("siebel","org.lwap.siebelplugin","field-mapping");
        param.put("name", Name);
        param.put("source_name", Source_name);
        param.put("value", Value);
        return param;
    }
}

/*
 * Created on 2007-7-19
 */
package org.lwap.siebelplugin;

import uncertain.composite.CompositeMap;

public class ParentBc {
    
    public String   Bc_name;
    public int Viewmode = -1;
    public Parameter[] Parameters;
    public CompositeMap toCompositeMap(){
        CompositeMap param = new CompositeMap("siebel","org.lwap.siebelplugin","parent-bc");
        param.put("bc_name", Bc_name);
        if (Parameters != null) {
			CompositeMap params = param.createChild("parameters");
			for (int i = 0; i < Parameters.length; i++)
				params.addChild(Parameters[i].toCompositeMap());
		}
        return param;
    }
}

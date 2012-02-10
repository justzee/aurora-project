package org.lwap.schema;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class SchemaFactoryModel {
	SchemaFactory fact;
	
	public SchemaFactoryModel(SchemaFactory fact) {
        this.fact = fact;
    }
    
    public void onCreateModel(ProcedureRunner runner) throws IOException,SAXException {
        CompositeMap context = runner.getContext(); 
        
        CompositeMap model = context.getChild("model");
        if (model==null) 
        {
        	model = context.createChild("model");
        }
        model.addChild(fact.categoryMap);
    }
}

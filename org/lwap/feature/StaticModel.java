/*
 * Created on 2005-10-8
 */
package org.lwap.feature;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/**
 * StaticModel
 * @author Zhou Fan
 * 
 */
public class StaticModel implements IFeature {
    
    CompositeMap static_model;
    
    public String Repeat_Count;
    public String Starting_Value;
    public String Sequence_Field;
    public String Path;
    
    List	childs;
    
    public List copyChilds(){
        List list = new LinkedList();
        Iterator it = childs.iterator();
        while(it.hasNext()){
            CompositeMap c = (CompositeMap)it.next();
            list.add(c.clone());
        }
        return list;
    }
    
    /**
     * @see uncertain.proc.IFeature#onAttach(uncertain.composite.CompositeMap)
     */
    public int attachTo(CompositeMap config, Configuration procConfig ) {
        static_model = config;
        childs = config.getChilds();
        return IFeature.NORMAL;
    }
    
    public void preCreateModel(ProcedureRunner runner){
        if(childs==null) return;
        CompositeMap context = runner.getContext(); 
        CompositeMap model = context.getChild("model");
        if( model == null ) model =  runner.getContext().createChild("model");
        /*
        if(Path!=null){ 
            CompositeMap p =(CompositeMap)model.getObject(Path); 
            if(p==null)
                model = model.createChildByTag(Path);
            else
                model = p;
        }
        */
        if(Path!=null) model = model.createChildByTag(Path);
        if(Repeat_Count!=null){
            Repeat_Count = TextParser.parse(Repeat_Count,context);
            int count = Integer.parseInt(Repeat_Count);
            int sValue=0;
            if(Starting_Value!=null)
                sValue = Integer.parseInt(TextParser.parse(Starting_Value,context));
            for(int i=0; i<count; i++){
                List newChilds = copyChilds();
                if(Sequence_Field!=null){
                    Integer sv = new Integer(sValue);
                    for(Iterator it = newChilds.iterator();it.hasNext();){
                        CompositeMap newItem = (CompositeMap)it.next();                        
                        newItem.put(Sequence_Field,sv);
                    }
                    sValue++;
                }
                model.addChilds(newChilds);
            }
        }
        else
            model.addChilds(static_model.getChilds());
        //System.out.println(model.toXML());
    }

}

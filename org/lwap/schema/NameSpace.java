/*
 * Created on 2005-10-11
 */
package org.lwap.schema;

/**
 * NameSpace
 * @author Zhou Fan
 * 
 */
public class NameSpace {
    
    public String Uri;
    public String Prefix;
    
    public NameSpace(){
        
    }

    public NameSpace(String uri, String prefix) {
        this.Uri = uri;
        this.Prefix = prefix;
    }
    
    public String toString(){
        return "xmlns:"+Prefix+"=\""+Uri+"\"";
    }

}

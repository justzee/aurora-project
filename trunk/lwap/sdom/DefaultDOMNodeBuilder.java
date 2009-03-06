/*
 * DefaultDOMNodeBuilder.java
 *
 * Created on 2001年9月18日, 下午7:57
 */

package sdom;

import java.util.HashMap;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DefaultDOMNodeBuilder  implements DOMNodeBuilder {
    
    static DefaultDOMNodeBuilder default_inst = new DefaultDOMNodeBuilder(null);
    
    /** HashMap to store prefix-package mapping */
    HashMap  packages = new HashMap();
    
    public static DefaultDOMNodeBuilder defaultInstance(){
        return default_inst;
    }
    
    public static final String DEFAULT_SET_PREFIX = "set_";
    String package_name = null;
    String set_prefix = DEFAULT_SET_PREFIX;

    /** Creates new DefaultDOMNodeBuilder */
    public DefaultDOMNodeBuilder(String class_package) {
        package_name = class_package;
    }
    
    public DefaultDOMNodeBuilder(String class_package, String set_pre){
        this(class_package);
        set_prefix = set_pre;
    }
    
   public void setPackageMapping( String prefix, String pkgName ){
   	 packages.put( prefix, pkgName);
   	}

    public DOMNode getDOMNode(String prefix,String name) {
        String pkg_name = (String)packages.get(prefix);
        if( pkg_name == null) pkg_name = package_name;
        if( pkg_name != null) {
        try{
            String cls_name = name;
            cls_name = pkg_name + '.' + cls_name;
            DOMNode node = (DOMNode)Class.forName(cls_name).newInstance();
            node.setName( prefix, name);
            return node;
        } catch( Exception ex){
            return new DOMNode( prefix, name );
        }  
       } 
       else return new DOMNode(prefix, name);
    }
    
    public String getAttributeSetMethod(String prefix,String attrib_name) {
        if( prefix != null) attrib_name = prefix + attrib_name;
        return set_prefix + attrib_name;
    }
    
}

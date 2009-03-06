/*
 * DOMNodeHandle.java
 *
 * Created on 2001年9月19日, 上午12:14
 */

package sdom;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DOMNodeHandle extends DefaultHandler {

    static Class[]     set_method_params = { String.class };
    
    DOMNodeBuilder        node_builder;
    DOMNode                   current_node = null;
    LinkedList                   node_stack = new LinkedList();
    
    void push( DOMNode node){
        node_stack.addFirst(node);
    }
    
    DOMNode pop(){
        DOMNode node = (DOMNode)node_stack.getFirst();
        node_stack.removeFirst();
        return node;
    }
    
    void addAttribs( DOMNode node, Attributes attribs){
        Class nodeCls = node.getClass();
        Object args[] = new Object[1];
        for( int i=0; i<attribs.getLength(); i++){
           args[0] = attribs.getValue(i);
           boolean set_success = false;
            try{                
              String mthd_name = node_builder.getAttributeSetMethod(attribs.getURI(i), attribs.getQName(i) );  
              Method set_method = nodeCls.getMethod( mthd_name, set_method_params);
              set_method.invoke(node, args);
              set_success = true;
            } catch( Exception ex){
            } finally{
               if( !set_success)   node.setAttribute( attribs.getQName(i), (String)args[0]);
            }
        }
    }
    

    /** Creates new DOMNodeHandle */
    public DOMNodeHandle( DOMNodeBuilder bd) {
       node_builder = bd;
    }
    
    public void startDocument(){
       current_node = null;
       node_stack.clear();
    }
    
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts)
	throws SAXException  {
            
            DOMNode node = node_builder.getDOMNode(namespaceURI, localName);
            if( node == null) return;
            
            addAttribs( node, atts);
            
            if( current_node == null){
               current_node = node;
            }else{
               current_node.addChild( node);
               push( current_node);
               current_node = node;
            }
    }
    
    public void endElement(String uri, String localName, String qName)
          throws SAXException{
             
             
             if( node_stack.size()>0)
              current_node = pop();
              
    }
    
    public void characters(char[] ch, int start, int length)  {
        if( length==0) return;
        String value = new String(ch, start, length);
        String nv = current_node.getValue();
        if( nv == null)  current_node.setValue( value );
        else current_node.setValue( nv+value);
    }

    /*
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {

    }
    
    public void endPrefixMapping(String prefix) throws SAXException {
    }
     */

        
    public DOMNode getRoot(){
           return current_node;
    }

}

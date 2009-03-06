/*
 * DOMNode.java
 *
 * Created on 2001年9月18日, 下午5:46
 */

package sdom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DOMNode  {
    
    public static final int DEFAULT_ATTRIB_COUNT = 40;
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
 
    
    protected HashMap    attrib = null;
   // protected HashMap
    protected List   childs = null;// = new LinkedList();
    protected String          prefixURI = null;
    protected String          localName = null;
    protected String          value = null;
   
    public DOMNode(){
    }
    
    public void setPrefixURI( String uri){
        if( uri != null)
            if( uri.length() == 0) uri = null;
        prefixURI = uri;
    }
    
    public void setLocalName( String name){
       localName = name;
    }
    
    public void setName( String prefix, String local){
       setPrefixURI( prefix);
       setLocalName( local);
    }
    
    /** Creates new DOMNode */
    public DOMNode( String name) {
        setLocalName( name);
    }
    
    public DOMNode( String prefix, String name){
        setName( prefix, name);
    }
    
    public DOMNode( String prefix, String name, int attrib_count){
        setName( prefix, name);
        attrib = new HashMap(attrib_count);
    }
    
    public String getAttribute(String key){
        if( attrib == null) return null;
        else return (String)attrib.get(key); 
    }
    
    public void setAttribute(String key, String value){
        getAttributesNotNull().put(key,value);
    }
    
    public Map getAttributes(){
        return attrib;
    }
    
    public Map getAttributesNotNull(){
        if(attrib == null)
            attrib = new HashMap(DEFAULT_ATTRIB_COUNT);
        return attrib;
    }
    
    public String getValue(){
        return value;
    }
    
    public void setValue(String v){
       value = v;
    }
    
    public void addChild( DOMNode child){
//       if( childs == null) childs = new LinkedList();
       getChildsNotNull().add(child);
    }
    
    public DOMNode getChild( Object obj){
        if( childs ==null) return null;
        
        Iterator it = childs.iterator();
        while( it.hasNext()){
            DOMNode    node = (DOMNode) it.next();
            if( node.equals(obj)) return node;
        }
        
        return null;
/*        
        int id= childs.indexOf(obj);
        if(id>=0) return (DOMNode)childs.get(id);
        else return null;
 */
    }
    
    public List getChilds(){        
        return childs;
    }
    
    public List getChildsNotNull(){
        if( childs == null) childs = new LinkedList();
        return childs;
    }
    
    public Iterator getChildIterator(){
        if( childs == null) return null;
        return childs.iterator();
    }
    
    public String getLocalName(){
    	return localName;
    }
        
    public String getName(){
        return localName;
    }
    
    public String getPrefixURI(){
         return prefixURI;
    }
    
    public String getQName(){
         /*
         if( localName == null) return null;
         else if( prefixURI == null ) return localName;
         else if( prefixURI.length() == 0) return localName;
         else return prefixURI + ":" + localName;
         */
         return localName;
    }
    
    public boolean equals( Object obj){
         if( obj instanceof DOMNode)
             return ((DOMNode)obj).getQName().equals(getQName());
         else if( obj instanceof String)
             return getQName().equals( obj);
         else return super.equals(obj);
    }
    
    String getSpace( int space){
       if( space<=0) return "";
       StringBuffer buf = new StringBuffer();
       for(int i=0; i<=space; i++) buf.append( ' ');
       return buf.toString();
    }
    
    public  String getAttributeText(Map attrib){
       if( attrib == null) return null;
       StringBuffer text = new StringBuffer();
       Iterator it = attrib.keySet().iterator();
       while( it.hasNext()){
           String key    = (String)it.next();
           String value =  XMLUtil.escape( (String)attrib.get(key));
           text.append(' ').append( key).append("=\"").append(value).append('"');
       }
       return text.toString();
    }
    
    public  String getChildText( Collection cds, int space ){
    	StringBuffer text = new StringBuffer();
    	if( cds == null) return null;
    	Iterator childs = cds.iterator();
                  while( childs.hasNext()){ 
                		DOMNode node = (DOMNode) childs.next();
                		text.append( LINE_SEPARATOR).append(node.toString(space+1));
                  }
    	return text.toString();
    	}
    
    public String toString(int space){
        StringBuffer text = new StringBuffer();
        String base_space =  getSpace(space);
        String attr = getAttributeText(attrib);
        String qname = getQName();
        String value = getValue();
        
        text.append(base_space);
        if(attr != null) text.append( '<'+ qname + attr ) ;
        else text.append( '<'+qname );
        
        String child_text =  getChildText(childs, space);
        if( child_text == null){
        	if( value != null) text.append('>').append(XMLUtil.escape(value)).append( XMLUtil.endTag(qname));
        	else text.append(" />");
        }else{
                text.append('>');
        	text.append(child_text).append( LINE_SEPARATOR).append( base_space).append( XMLUtil.endTag( qname));
        }
        
        return text.toString();
    }
    
    public String toString(){
        return toString(0);
    }
    
    public DOMNode insertChilds( DOMNode root){
         getChildsNotNull().addAll(0,root.getChilds());
         return this;
    }
    
    public DOMNode appendChilds( DOMNode root){
         getChildsNotNull().addAll(root.getChilds());        
         return this;
    }

}

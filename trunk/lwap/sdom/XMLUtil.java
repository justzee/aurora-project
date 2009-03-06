/*
 * XMLUtil.java
 *
 * Created on 2001年9月18日, 下午6:58
 */

package sdom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class XMLUtil extends Object {
  
  public static String DEFAULT_ENCODING = "utf-8";

  public static String getXMLDecl(String encoding){
   if (encoding == null) encoding = DEFAULT_ENCODING;
   return "<?xml version=\"1.0\" encoding = \"" + encoding + "\"?>\r\n";
  }
 
/*  
//  static DocumentBuilder domBuilder = null;
  static DocumentBuilderFactory builderFactory = null;
  
   public static DocumentBuilder getBuilder(){
     try{
      if( builderFactory == null){
         builderFactory =  DocumentBuilderFactory.newInstance();
         builderFactory.setValidating(false);
         builderFactory.setIgnoringComments(true);
         builderFactory.setIgnoringElementContentWhitespace(true);
         builderFactory.setCoalescing(true);
      }
         return builderFactory.newDocumentBuilder();     
      } catch(Exception ex){
         ex.printStackTrace();
         return null;
      }
  }
  */
  
  public static String getAttribute( NamedNodeMap attribs, String key){
    if( attribs == null ) return null;
    else {
     Node attr = attribs.getNamedItem(key);
     if(attr == null) return null;
     else return attr.getNodeValue();
   }
  }

  public static String startTag(String element){
    return "<" + element + ">";
  }
  
  public static String endTag(String element){
    return "</" + element + ">";
  }
  
  public static String cdata(String value){
    return  "<![CDATA[" + value + "]]>";
  }
  
  public static String getAttrib(String key, String value){
    return key+'='+'"'+escape(value) + '"';
  }
  
  public static String escape(String value){
    
     StringBuffer dom  = new StringBuffer();
     for(int i=0; i<value.length(); i++){
       	char ch = value.charAt(i); 
       	if( ch=='<') dom.append("&lt;");
       	else if(ch=='>') dom.append("&gt;");
       	else if(ch=='&') dom.append("&amp;");
        else if(ch=='"') dom.append("&quot;");
        else if(ch=='\'') dom.append("&apos;");
       	else dom.append(ch);
       }
    return dom.toString();
  }
  
}

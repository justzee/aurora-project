/*
 * DOMNodeBuilder.java
 *
 * Created on 2001年9月18日, 下午7:34
 */

package sdom;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DocumentParser  {
    
    DOMNodeHandle            handle;
    DOMNodeBuilder            node_builder;
    XMLReader                     xmlreader;

    
    public static DocumentParser newInstance(){
         return new DocumentParser( DefaultDOMNodeBuilder.defaultInstance() );
    }    
    
    public static DocumentParser newInstance( String class_package){
        return new DocumentParser( new DefaultDOMNodeBuilder(class_package));
    }
    
    public static DocumentParser newInstance( DOMNodeBuilder bd){
         return new DocumentParser(bd);
    }

    /** Creates new DOMNodeBuilder */
    public DocumentParser(DOMNodeBuilder bd) {
        node_builder = bd;
        handle = new DOMNodeHandle( node_builder);
        try{
            xmlreader = new SAXParser();
 /*
            xmlreader.setFeature("http://xml.org/sax/features/namespaces", true);
            xmlreader.setFeature("http://xml.org/sax/features/validation", false);
  */
            xmlreader.setContentHandler( handle);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public DOMNode parseFile( String file_name) throws SAXException, IOException {
       return parse( new InputSource( new FileReader( file_name)));
    }
    
    public DOMNode parseURI( String uri) throws SAXException, IOException {
         xmlreader.parse(uri);
         return handle.getRoot();
    }
    
    public DOMNode parse( InputStream stream )  throws SAXException, IOException {
          return parse( new InputSource( stream));
    }
    
    public  DOMNode parse( Reader reader )  throws SAXException, IOException {
          return parse( new InputSource( reader));
    }
    
    public DOMNode  parse( InputSource input) throws SAXException, IOException {
          if( input == null) throw new SAXException( "input source null");
          if( xmlreader == null) throw new SAXException(" reader null");
          try{
          xmlreader.parse(input);
          return handle.getRoot();
          } catch( SAXParseException ex){
             System.out.println( "location:"+ex.getLineNumber() +"/"+ ex.getColumnNumber()) ;
             throw ex;
          } 
    }
    

}

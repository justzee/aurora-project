/*
 * DefaultErrorHandler.java
 *
 * Created on 2001年9月19日, 上午12:55
 */

package sdom;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DefaultErrorHandler  implements ErrorHandler {

    String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
    }
        
    public static DefaultErrorHandler defaultInstance = new  DefaultErrorHandler();
    /** Creates new DefaultErrorHandler */
    public DefaultErrorHandler() {
    }

  
     public void warning(SAXParseException spe) throws SAXException {
         //  out.println("Warning: " + getParseExceptionInfo(spe));
     }
        
      public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
      }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
       }

}

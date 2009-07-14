/*
 * Created on 2009-6-9
 */
package uncertain.demo.composite;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

public class CompositeMapDemo {

    public static void demoBasicFeatures(){
        // Handle as Map
        CompositeMap mail = new CompositeMap("mail");        
        mail.put("title", "This is a test");
        mail.put("from", "yourname@yahoo.com");
        mail.put("to", "sombody@gmail.com");
        mail.setNameSpace("demo", "http://uncertain.org/demo");
        System.out.println("A simple map:");
        System.out.println(mail.toXML());
        // Child operations
        CompositeMap attachment1 = mail.createChild("attachment");
        attachment1.put("file", "myphoto.jpg");
        attachment1.putInt("size", 2048);
        // create new CompositeMap and add to parent
        CompositeMap attachment2 = new CompositeMap("attachment");
        attachment2.put("file", "somedoc.zip");
        mail.addChild(attachment2);
        // set text
        CompositeMap body = new CompositeMap("body");
        body.setText("A demo mail");
        mail.addChild(body);
        System.out.println("After create child");
        System.out.println(mail.toXML());
        // iteration
        System.out.println("Iterate each child:");
        mail.iterate( new IterationHandle(){
            
            public int process( CompositeMap map){
                System.out.println(map.getName());
                return IterationHandle.IT_CONTINUE;
            }
            
        }, true);
        // access by XPath syntax
        Object o = mail.getObject("attachment/@file");
        System.out.println("attachment/@file = "+o);
        mail.putObject("childs/child/@item", "value", true);
        System.out.println("After putObject:\r\n");
        System.out.println(mail.toXML());
        
    }
    
    public static void demoLoadCompositeMap()
        throws IOException, SAXException
    {
        // load from class path
        CompositeLoader loader = new CompositeLoader();
        CompositeMap mail = loader.loadFromClassPath("uncertain.demo.composite.MailDemo");
        System.out.println("mail load from file:");
        System.out.println(mail.toXML());
        // load from string
        String xml_text = "<mail title=\"mail demo from string\" />";
        mail = loader.loadFromString(xml_text);
        System.out.println(mail.toXML());
        // construct from base directory and load from file
        URL url = Thread.currentThread().getContextClassLoader().getResource("uncertain/demo/composite");
        if(url==null) throw new IOException();
        String base_path = url.getFile();
        CompositeLoader loader2 = new CompositeLoader(base_path);
        CompositeMap mail2 = loader2.loadByFile("MailDemo.xml");
        System.out.println("Load by file:");
        System.out.println(mail2.toXML());
    }
    /**
     * @param args
     */
    public static void main(String[] args) 
        throws Exception
    {
        demoBasicFeatures();
        demoLoadCompositeMap();
    }

}

/*
 * Created on 2009-6-9
 */
package uncertain.demo.ocm;

import java.util.List;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.ocm.PackageMapping;

public class OCMDemo {
    
    static OCManager    ocManager;
    
    public static class SomeClassWithAttachments {
        
        List    attachments;

        public List getAttachments() {
            return attachments;
        }

        public void setAttachments(List attachments) {
            this.attachments = attachments;
        }
        
    }

    /**
     * @param args
     */
    public static void main(String[] args) 
        throws Exception
    {
       // load CompositeMap from xml file
       CompositeLoader loader = new CompositeLoader();
       CompositeMap map = loader.loadFromClassPath("uncertain.demo.composite.MailDemo");
       System.out.println("CompositeMap loaded:");
       System.out.println(map.toXML());
       // setup package mapping
       ocManager = OCManager.getInstance();
       ClassRegistry classReg = ocManager.getClassRegistry();
       classReg.addPackageMapping( new PackageMapping("uncertain.demo.ocm","uncertain.demo.ocm"));
       // create object from CompositeMap
       Mail mail = (Mail)ocManager.createObject(map);       
       System.out.println("Mail title:"+mail.getTitle());
       System.out.println("Mail body:"+mail.getBodyText());
       Attachment[] atm = mail.getAttachments();
       System.out.println("Attachment count:"+atm.length);
       for(int i=0; i<atm.length; i++){
           System.out.println("No."+i+": file="+atm[i].getFile());
       }
       // populate another object
       SomeClassWithAttachments foo = new SomeClassWithAttachments();
       ocManager.populateObject(map, foo);
       List atm_list = foo.getAttachments();
       System.out.println("After populate:");
       System.out.println(atm_list);  
       
       // setup IObjectRegistry 
       SmtpHost host = new SmtpHost("yahoo.com", 25);
       ObjectRegistryImpl objReg = new ObjectRegistryImpl();
       objReg.registerInstance(SmtpHost.class, host);
       ocManager.setObjectCreator(objReg);
       // create object with constructor arguments
       Mail mail2 = (Mail)ocManager.createObject(map);
       System.out.println("SMTP host:"+mail2.getSmtpHost().getAddress());
    }

}

/*
 * Created on 2009-6-9
 * $Id$
 */
package uncertain.demo.event;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.demo.ocm.Mail;
import uncertain.event.Configuration;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.FeatureAttach;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;

public class EventDemo {
    
    static OCManager mOcManager;
    
    static void setupOcManager()
        throws ClassNotFoundException
    {
        mOcManager = OCManager.getInstance();
        ClassRegistry reg = mOcManager.getClassRegistry();
        reg.addPackageMapping(new PackageMapping("uncertain.demo.ocm",
        "uncertain.demo.ocm"));        
    }

    public static void doBasicEventDemo() throws Exception {
        System.out.println("1. A simple event handle demo");
        CompositeLoader loader = CompositeLoader.createInstanceForOCM();
        CompositeMap mail_config = loader
                .loadFromClassPath("uncertain.demo.composite.MailDemo");
        Mail mail = (Mail) mOcManager.createObject(mail_config);
        
        FeatureAttach fa = new FeatureAttach("uncertain.demo.ocm", "mail",
                AttachmentProcessor.class);
        mOcManager.getClassRegistry().addFeatureAttach(fa); 
        
        Configuration config = new Configuration(mOcManager);
        config.loadConfig(mail_config);
        System.out.println("To fire event with one participant");
        config.fireEvent("MailSend", new Object[] { mail });

        mOcManager.getClassRegistry().addFeatureAttach(new FeatureAttach("uncertain.demo.ocm", "mail",
                SpamScanner.class)); 
        config = new Configuration(mOcManager);
        config.loadConfig(mail_config);        
        System.out.println("Now fire event again with two participants");
        config.fireEvent("MailSend", new Object[] { mail });

    }
    
    public static void demoEventLogging() throws Exception {
        
    }

    public static void main(String[] args) throws Exception {
        setupOcManager();        
        doBasicEventDemo();
    }

}

/*
 * Created on 2007-6-14
 */
package org.lwap.plugin.mail;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class SendMail extends AbstractEntry {
    
    String  Title;
    String  Body;
    String  Mailto;
    
    String mailfrom;
    
    MailSettings    settings;
    
    public SendMail(MailSettings settings){
        this.settings = settings;
    }

    /**
     * @return the mailfrom
     */
    public String getMailfrom() {
        return mailfrom;
    }

    /**
     * @param mailfrom the mailfrom to set
     */
    public void setMailfrom(String mailfrom) {
        System.out.println("set mailfrom:"+mailfrom);        
        this.mailfrom = mailfrom;
    }

    public void run(ProcedureRunner runner) throws Exception {
        
        CompositeMap context = runner.getContext();
        Title = TextParser.parse(Title, context);
        System.out.println(context.toXML());
        /*
        System.out.println(settings.SmtpHost);
        System.out.println(Title);
        */
    }

}

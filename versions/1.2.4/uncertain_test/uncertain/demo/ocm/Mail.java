/*
 * Created on 2009-6-9
 */
package uncertain.demo.ocm;

import uncertain.composite.CompositeMap;

public class Mail {
    
    String  title;
    String  from;
    String  to;
    
    CompositeMap  body;
    Attachment[]    attachments;
    
    SmtpHost    smtpHost;
    
    /** Default constructor */
    public Mail(){
        
    }
    
    /** Constructor with an SmtpHost instance */
    public Mail( SmtpHost host ){
        this.smtpHost = host;
    }    
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public void addBody( CompositeMap body ){
        this.body = body;
    }
    
    public CompositeMap getBody(){
        return body;
    }
    
    public String getBodyText(){
        return body.getText();
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachment[] attachments) {
        this.attachments = attachments;
    }
    
    public SmtpHost getSmtpHost(){
        return smtpHost;
    }


}

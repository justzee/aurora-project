/*
 * Created on 2009-6-9
 */
package uncertain.demo.event;

import uncertain.demo.ocm.Attachment;
import uncertain.demo.ocm.Mail;

public class AttachmentProcessor {
    
    public void onMailSend( Mail mail ){
        Attachment[] atm = mail.getAttachments();
        if(atm!=null)
            System.out.println("[AttachmentProcessor] total "+atm.length+" attachments");
    }

}

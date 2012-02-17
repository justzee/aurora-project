/*
 * Created on 2010-4-12 下午12:05:32
 * $Id$
 */
package uncertain.demo.event;

import uncertain.demo.ocm.Mail;
import uncertain.event.EventModel;

public class SpamScanner {
    
    String title;
    
    public int preMailSend( Mail mail ){
        String from = mail.getFrom();
        if(from.indexOf("somebody")>=0){
            System.out.println("[SpamScanner] Mail with title:" + getTitle());
            System.out.println("[SpamScanner] this is a spam");
            return EventModel.HANDLE_STOP;
        }else{
            System.out.println("[SpamScanner] This mail passed spam scan");
        }
        return EventModel.HANDLE_NORMAL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

/*
 * Created on 2007-6-14
 */
package org.lwap.plugin.mail;

import uncertain.core.IGlobalInstance;

public class MailSettings implements IGlobalInstance{
    
    public int          SmtpPort;
    public String       SmtpHost;
    public boolean      SmtpAuthenticate;
    public String       DefaultMailFrom;
    public String       DefaultMailFromPassword;
    /*
    public void onInitialize(){
        System.out.println("mail init:Mail "+SmtpHost);
    }
    */

}

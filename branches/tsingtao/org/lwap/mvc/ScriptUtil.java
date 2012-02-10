/*
 * Created on 2008-3-26
 */
package org.lwap.mvc;

public class ScriptUtil {
    
    public static String encodeJSContent(String input, boolean use_quot){
       if(use_quot)
           input = input.replaceAll("\\\"", "\"+String.fromCharCode(34)+\"");
       else
           input = input.replaceAll("\\\'", "\"+String.fromCharCode(44)+\"");
       input = input.replaceAll("\\\\", "\\\\\\\\");
       input = input.replaceAll("\\r", "\\\\"+"r");
       input = input.replaceAll("\\n", "\\\\"+"n");
       return input;
    }
  
    public static void main(String[] args){
        String s = "I said: \"you \\ should \"\r\n,right now";
        System.out.println(s);
        System.out.println(encodeJSContent(s, true));
    }


}

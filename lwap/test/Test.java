/*
 * Created on 2009-4-21
 */
package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("myfile.*.log");
        String s = "myfile200801.log";
        Matcher m = pattern.matcher(s);
        System.out.println(m.matches());

    }

}

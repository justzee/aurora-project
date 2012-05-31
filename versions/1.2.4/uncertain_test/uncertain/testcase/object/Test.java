/*
 * Created on 2005-6-27
 */
package uncertain.testcase.object;

import uncertain.composite.*;
import java.io.*;
/**
 * Test
 * @author Zhou Fan
 * 
 */
public class Test {

    /**
     * 
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }
    public static void main(String[] args) {
        File[] f = new File[5];
        f[0] = new File("c:/temp");
        f[1] = new File("c:\\temp/");
        f[2] = new File("c:/temp/");
        f[3] = new File("c:\\temp\\");
        f[4] = new File("c:\\temp");
        for(int i=0; i<5; i++) System.out.println("File"+i+":"+f[i].getPath());
    }

    public static void main1(String[] args) {
    
        CompositeMap m = new CompositeMap("test");
        m.put("Test1", "value");
        m.put("Test2","");
        m.put("Test3",null);
        System.out.println(m.entrySet());
        System.out.println(m.keySet());
        System.out.println(m.toXML());

        }
}

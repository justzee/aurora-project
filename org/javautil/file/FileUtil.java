/*
 * FileUtil.java
 *
 * Created on 2001年12月20日, 下午5:06
 */

package org.javautil.file;

import java.io.File;
/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class FileUtil  {

    public static String getNameNoExt( File file){
       String name= file.getName();
       return name.substring(0, name.lastIndexOf('.'));
    }
    
    public static File getDiffExt( File file, String ext){
        return new File( file.getParent(), getNameNoExt(file) + '.' + ext);
    }
    
    public static void main(String[] args) throws Exception{
        System.out.println( getDiffExt( new File("c:/winnt/js.bat"), "exe"));
    }
    

}

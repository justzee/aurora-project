package org.javautil;

import java.net.URL;

public class ClassFind {

public static void main(String[] args) throws Exception {
	if( args.length<1){
		System.out.println("usage: ClassFind <full_class_name>");
		return;
		}
	String vl = args[0].replace('.','/');
	vl += ".class";
	ClassLoader loader = ClassLoader.getSystemClassLoader() ;
	System.out.println("using classloader "+loader.getClass().getName());
	URL file = loader.getResource(vl);
	if( file == null) System.out.println("can't find "+args[0]);
	else System.out.println(args[0] + " is from " + file.toString());
	}

}
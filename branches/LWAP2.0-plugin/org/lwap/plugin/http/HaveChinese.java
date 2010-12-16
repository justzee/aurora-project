package org.lwap.plugin.http;

public class HaveChinese {
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
   public static boolean hasChinese(String s){
	 
	   int i=0;
	   char[] ch = s.toCharArray();
	   
	   for (char c :ch ){
		   if (HaveChinese.isChinese(c))  
		   break;
		   else{
			   i++;
			   continue;}
	   }
        return i<ch.length;
	   
   }
 
}

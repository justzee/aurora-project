/*
 * WildcardFilter.java
 *
 * Created on 2001年12月20日, 上午11:27
 */

package org.javautil.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
public class WildcardFilter implements FilenameFilter{  
      private String filter;    
      
      public WildcardFilter(String filter) {    
          this.filter = filter.toLowerCase();
      }
      
      public boolean accept(File dir, String name) {    
          name = name.toLowerCase();      
          int idx = 0;    
          boolean wild = false;
          StringTokenizer tokens = new StringTokenizer(filter, "*", true);
          while (tokens.hasMoreTokens()) {
              String token = tokens.nextToken();
              if (wild == true) {
                  wild = false;
                  if (name.indexOf(token, idx) > idx)
                      idx = name.indexOf(token, idx);
              }
              if (token.equals("*"))
                  wild = true;
              else
                  if (name.indexOf(token, idx) == idx)
                      idx += token.length();
                  else
                      break;
              if (!tokens.hasMoreTokens()) {
                  if (token.equals("*") || name.endsWith(token))
                      idx = name.length();
              }
          }
          return (idx == name.length());
      }    

}
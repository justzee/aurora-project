/*
 * Created on 2014年12月23日 下午8:43:16
 * $Id$
 */
package pipe.test;

import java.util.Date;

public class LogRecord {

    Date date;
    String source;
    String content;


    public LogRecord(Date date, String source, String content) {
        super();
        this.date = date;
        this.source = source;
        this.content = content;
    }
    
    public Date getDate() {
        return date;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    public String toString(){
        return "[" +date +"]["+ source +"] " + content;
    }


}

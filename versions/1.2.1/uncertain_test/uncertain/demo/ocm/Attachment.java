/*
 * Created on 2009-6-9
 */
package uncertain.demo.ocm;

public class Attachment {
    
    String  file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    public String toString(){
        return "attachment["+file+"]";
    }

}

/*
 * Created on 2009-12-4 下午01:18:07
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

public class GlobalBean {
    
    String app_name;
    
    public GlobalBean( String name ){
        app_name = name;
    }

    public String getApplicationName() {
        return app_name;
    }

    public void setApplicationName(String name) {
        this.app_name = name;
    }

}

/*
 * Created on 2009-12-4 下午01:20:51
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

public class ServiceListener {
    
    /**
     * @param global_bean
     */
    public ServiceListener(GlobalBean global_bean) {
        super();
        this.global_bean = global_bean;
    }

    GlobalBean      global_bean;
    
    public void onServiceInit( Event evt ){
        evt.setSender("From "+global_bean.getApplicationName());
    }


}

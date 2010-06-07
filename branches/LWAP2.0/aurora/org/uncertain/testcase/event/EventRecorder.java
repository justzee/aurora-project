/*
 * Created on 2009-12-4 下午06:12:49
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

import uncertain.event.EventModel;
import uncertain.proc.IEventListener;
import uncertain.proc.ProcedureRunner;

public class EventRecorder implements IEventListener {
    
    /**
     * @param name
     */
    public EventRecorder(String name, StringBuffer content) {
        super();
        this.name = name;
        this.content = content;
    }

    String name;
    
    StringBuffer    content;

    public int onEvent(ProcedureRunner runner, int sequence, String event_name) {
        if(sequence==EventModel.ON_EVENT)
            content.append(name).append('.').append(event_name).append("\r\n");
        return EventModel.HANDLE_NORMAL;
    }
    
    public void onBeginService( String p1, Integer p2 ){
        content.append(name).append(".handle.onBeginService(").append(p1).append(",").append(p2).append(")\r\n");
    }

}

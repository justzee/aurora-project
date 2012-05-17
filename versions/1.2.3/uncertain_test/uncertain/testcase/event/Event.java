/*
 * Created on 2009-12-4 下午01:18:52
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

/**
 * A simple class to transfer info. in event dispatch
 * Event
 */
public class Event {

    /**
     * @param sender
     * @param name
     */
    public Event(String sender, String name) {
        super();
        this.sender = sender;
        this.name = name;
    }

    String sender;
    String name;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

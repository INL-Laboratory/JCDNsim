package entities.physical;

import entities.logical.Event;
import entities.logical.EventType;
import entities.logical.EventsQueue;
import entities.logical.Segment;

public class Client extends EndDevice {
    public static final float requestSize = (float) 0.05;
    private int generatedId;
    private Link link;

    public Client(){
        generatedId = 0;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public void handleEvent(Event event) throws Exception {
        if(event.getType() == EventType.sendReq){
            //TODO: Add needed file to optionalContent of segment
            Segment segment = new Segment(generateId(), this, null, requestSize);
            EventsQueue.addEvent(
                    new Event<>(EventType.sendData, link, event.getTime(),this, segment)
            );
        }
        if(event.getType() == EventType.receiveData){
            //TODO: ADD to log. we need a map of unanswered requests id and created time of request
        }
    }

    private int generateId(){
        generatedId++;
        return generatedId;
    }
}

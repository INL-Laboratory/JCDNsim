package entities.physical;

import entities.logical.*;

public class Client extends EndDevice {
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
            sendFileRequest(event);
        }
        if(event.getType() == EventType.receiveSegment){
            //TODO: ADD to log. we need a map of unanswered requests id and created time of request
        }
    }

    private void sendFileRequest(Event event) {
        //TODO: Add needed file to optionalContent of segment
        Server dstServer = (Server)link.getOtherEndPoint(this);
        generateId();
        Segment segment = new Segment(
                generatedId, this, dstServer , DefaultValues.REQUEST_SIZE,
                SegmentType.Request, new Request(this,dstServer, 0 , generatedId) //todo: this id should be generated
        );
        EventsQueue.addEvent(
                new Event<>(EventType.sendData, link, event.getTime(),this, segment)
        );
    }

    private int generateId(){
        generatedId++;
        return generatedId;
    }


}

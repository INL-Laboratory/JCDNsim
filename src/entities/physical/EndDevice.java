package entities.physical;

import entities.logical.*;

public abstract class EndDevice extends IEventHandler{
    /**
     * Instead of using IP addresses, by this number we specify the servers and clients.
     */
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    protected void sendData(float time, Link link, Segment segment) {
        EventsQueue.addEvent(
                new Event<>(EventType.sendData, link, time, this , segment)
        );
    }

    public boolean receiveData(float time, Segment segment, Link link){
        if(!isReceivedDataValid(link))
            return false;
        parseReceivedSegment(time, segment);
        return true;
    }

    protected boolean isThisDeviceDestined(Segment segment) {
        return segment.getDestination().equals(this);
    }

    protected abstract boolean isReceivedDataValid(Link link);

    protected abstract void parseReceivedSegment(float time, Segment segment);
}

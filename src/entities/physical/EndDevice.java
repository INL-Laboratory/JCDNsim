package entities.physical;

import entities.logical.*;
import entities.utilities.logger.Logger;

public abstract class EndDevice extends IEventHandler{
    /**
     * Instead of using IP addresses, by this number we specify the servers and clients.
     */
    protected int number;

    public EndDevice(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    protected void sendData(float time, Link link, Segment segment) {
        Logger.print(this+ " sends "+ segment  + " at time "+ time+" to " + segment.getDestination() + " through link" + link,time);
        EventsQueue.addEvent(
                new Event<>(EventType.sendData, link, time, this , segment)
        );
    }

    public boolean receiveData(float time, Segment segment, Link link) throws Exception{
        if(!isReceivedDataValid(link))
            return false;
        Logger.print(this + " received "+ segment + " from Link " + link,time);
        parseReceivedSegment(time, segment);
        return true;
    }

    protected boolean isThisDeviceDestined(Segment segment) {
        return segment.getDestination().equals(this);
    }

    protected abstract boolean isReceivedDataValid(Link link);

    protected abstract void parseReceivedSegment(float time, Segment segment) throws Exception;
}

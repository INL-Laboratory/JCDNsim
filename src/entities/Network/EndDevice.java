/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import entities.Setting.AlgorithmData;
import entities.Setting.EventType;
import entities.Simulator.*;

import java.util.Objects;

public abstract class EndDevice extends IEventHandler{

    protected int number; //Instead of using IP addresses, by this number we specify the servers and clients.

    EventsQueue eventsQueue;
    AlgorithmData algorithmData;

    public EndDevice(int number, EventsQueue eventsQueue, AlgorithmData algorithmData) {
        this.number = number;
        this.eventsQueue = eventsQueue;
        this.algorithmData = algorithmData;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    protected void sendData(float time, Link link, Segment segment) {
//        Logger.print(this+ " sends "+ segment  + " at time "+ time+" to " + segment.getDestination() + " through link" + link,time);
        eventsQueue.addEvent(
                new Event<>(EventType.sendData, link, time, this , segment)
        );
    }

    boolean receiveData(float time, Segment segment, Link link) throws Exception{
        if(!isReceivedDataValid(link))
            return false;
//        Logger.print(this + " received "+ segment + " from Link " + link,time);
        parseReceivedSegment(time, segment);
        return true;
    }



    protected boolean isThisDeviceDestined(Segment segment) {
        return segment.getDestination().equals(this);
    }

    protected abstract boolean isReceivedDataValid(Link link);

    protected abstract void parseReceivedSegment(float time, Segment segment) throws Exception;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndDevice endDevice = (EndDevice) o;
        return number == endDevice.number;
    }

    @Override
    public int hashCode() {

        return Objects.hash(number);
    }
}

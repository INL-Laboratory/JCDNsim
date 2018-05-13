package entities.physical;

import entities.logical.*;

import java.util.ArrayList;
import java.util.List;

public class Link extends IEventHandler{
    private EndDevice endPointA;
    private EndDevice endPointB;
    private float propagationDelay;
    private float bw;
    private List<Segment> segmentsFromA;
    private List<Segment> segmentsFromB;
    private boolean isSendingFromA;
    private boolean isSendingFromB;
    private int weight = 1; //number of intervening ASes between endpointA and B+1

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Link(float propagationDelay, float bw){
        segmentsFromA = new ArrayList<>();
        segmentsFromB = new ArrayList<>();
        this.propagationDelay = propagationDelay;
        this.bw = bw;
    }

    public Link(EndDevice endPointA, EndDevice endPointB, float propagationDelay, float bw, int weight) {
        this(propagationDelay, bw);
        this.endPointA = endPointA;
        this.endPointB = endPointB;
        this.weight = weight;
    }

    public float getPropagationDelay() {
        return propagationDelay;
    }

    public void setPropagationDelay(float propagationDelay) {
        this.propagationDelay = propagationDelay;
    }

    public float getBw() {
        return bw;
    }

    public void setBw(float bw) {
        this.bw = bw;
    }

    public EndDevice getEndPointA() {
        return endPointA;
    }

    public void setEndPointA(EndDevice endPointA) {
        this.endPointA = endPointA;
    }

    public EndDevice getEndPointB() {
        return endPointB;
    }

    public void setEndPointB(EndDevice endPointB) {
        this.endPointB = endPointB;
    }

    @Override
    public void handleEvent(Event event) throws Exception {if(!event.getRelatedEntity().equals(this)){
            throw new Exception("");
        }

        if(event.getType() == EventType.sendData){
            if(event.getCreator().equals(endPointA))
                segmentsFromA.add((Segment) event.getOptionalData());
            else segmentsFromB.add((Segment) event.getOptionalData());

            checkForSendData(event.getTime());
        }
        else if(event.getType() == EventType.dataSent){
            //Remove sent segment from queue
            Segment sentSegment = (Segment) event.getOptionalData();
            sentSegment.increaseToleratedCost(this.weight);
            boolean isInA = segmentsFromA.remove(sentSegment);
            boolean isInB = segmentsFromB.remove(sentSegment);

            if(!isInA && !isInB)
                throw new Exception(sentSegment+ " not found in " + this);

            if(isInA)
                isSendingFromA = false;
            else isSendingFromB = false;

            //Create Event for receiver
            Event e = new Event<>(EventType.receiveSegment,
                    (isInA)? endPointB : endPointA, event.getTime(), this, sentSegment );
            EventsQueue.addEvent(e);

            //SendNextSegment
            checkForSendData(event.getTime());
        }
    }

    private void checkForSendData(float time) {
        if(segmentsFromA.size()>0 && !isSendingFromA){
            float eventTime = time + propagationDelay + segmentsFromA.get(0).getSize()/bw;
//            float eventTime = time + propagationDelay ;
            Event<Link> event = new Event<>(EventType.dataSent, this, eventTime, this, segmentsFromA.get(0));
            EventsQueue.addEvent(event);

            isSendingFromA = true;
        }
        else if(segmentsFromB.size()> 0 && !isSendingFromB){
            float eventTime = time + propagationDelay + segmentsFromB.get(0).getSize()/bw;
//            float eventTime = time + propagationDelay ;
            Event<Link> event = new Event<>(EventType.dataSent, this, eventTime, this, segmentsFromB.get(0));
            EventsQueue.addEvent(event);

            isSendingFromB = true;
        }
    }

    public EndDevice getOtherEndPoint(EndDevice endDevice){
        return endDevice.equals(endPointA)?endPointB:endPointB;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Link{");
        sb.append("endPointA=").append(endPointA.toString());
        sb.append(", endPointB=").append(endPointB.toString());
        sb.append('}');
        return sb.toString();
    }
}

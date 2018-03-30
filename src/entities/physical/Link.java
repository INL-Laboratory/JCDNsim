package entities.physical;

import entities.logical.Event;
import entities.logical.IEventHandler;

public class Link extends IEventHandler{
    private EndDevice endPoint1;
    private EndDevice endPoint2;
    private float propagationDelay;
    private float bw;

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

    public EndDevice getEndPoint1() {
        return endPoint1;
    }

    public void setEndPoint1(EndDevice endPoint1) {
        this.endPoint1 = endPoint1;
    }

    public EndDevice getEndPoint2() {
        return endPoint2;
    }

    public void setEndPoint2(EndDevice endPoint2) {
        this.endPoint2 = endPoint2;
    }

    @Override
    public void handleEvent(Event event) throws Exception {
        if(!event.getRelatedEntity().equals(this)){
            throw new Exception("");
        }
    }
}

package entities.logical;

import entities.physical.EndDevice;

public class Segment {
    //Each client generate id for each request, and server put this id in response segment
    //So client can map request and response together
    private int id;
    private EndDevice source;
    private EndDevice destination;
    private float size;
    private String optionalContent;

    public Segment(int id, EndDevice source, EndDevice destination, float size){
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = size;
    }

    public Segment(int id, EndDevice source, EndDevice destination, float size, String optionalContent){
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.optionalContent = optionalContent;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EndDevice getSource() {
        return source;
    }

    public void setSource(EndDevice source) {
        this.source = source;
    }

    public EndDevice getDestination() {
        return destination;
    }

    public void setDestination(EndDevice destination) {
        this.destination = destination;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getOptionalContent() {
        return optionalContent;
    }

    public void setOptionalContent(String optionalContent) {
        this.optionalContent = optionalContent;
    }
}

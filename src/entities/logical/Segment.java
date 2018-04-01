package entities.logical;

import entities.physical.EndDevice;

public class Segment {
    //Each client generate id for each request, and server put this id in response segment
    //So client can map request and response together
    private final int id;
    private EndDevice source;
    private EndDevice destination;
    private float size;
    private Request optionalContent;
    private SegmentType segmentType;

    public Segment(int id, EndDevice source, EndDevice destination, float size ,SegmentType segmentType){
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.segmentType = segmentType ;

    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public Segment(int id, EndDevice source, EndDevice destination, float size, SegmentType segmentType , Request optionalContent){
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.optionalContent = optionalContent;
        this.segmentType = segmentType;

    }


    public int getId() {
        return id;
    }


    public EndDevice getSource() {
        return source;
    }

//    public void setSource(EndDevice source) {
//        this.source = source;
//    }

    public EndDevice getDestination() {
        return destination;
    }

//    public void setDestination(EndDevice destination) {
//        this.destination = destination;
//    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Request getOptionalContent() {
        return optionalContent;
    }

    public void setOptionalContent(Request optionalContent) {
        this.optionalContent = optionalContent;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }
}

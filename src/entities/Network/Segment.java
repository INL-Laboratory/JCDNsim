/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import entities.Setting.SegmentType;

public class Segment {
    //Each client generate id for each request, and server puts this id in response segment
    //So client can map request and response together
    private final int id;
    private EndDevice source;
    private EndDevice destination;
    private float size;
    private int toleratedCost;
    private Object optionalContent;
    private SegmentType segmentType;

    public Segment(int id, EndDevice source, EndDevice destination, float size ,SegmentType segmentType, int toleratedCost){
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.segmentType = segmentType ;
        this.toleratedCost = toleratedCost;
        if(this.optionalContent instanceof Request){
            ((Request) this.optionalContent).setToleratedCost(toleratedCost);
        }

    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public Segment(int id, EndDevice source, EndDevice destination, float size, SegmentType segmentType , Object optionalContent, int toleratedCost){
        this(id,source,destination,size,segmentType,toleratedCost);
        this.optionalContent = optionalContent;

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

    public Object getOptionalContent() {
        return optionalContent;
    }

//    public void setOptionalContent(Request optionalContent) {
//        this.optionalContent = optionalContent;
//    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Segment{");
        sb.append("id=").append(id);
        sb.append(", segmentType=").append(segmentType);
        sb.append('}');
        return sb.toString();
    }
    public void increaseToleratedCost(int newCost){
        toleratedCost += newCost;
        if(this.optionalContent instanceof Request){
            ((Request)this.optionalContent).setToleratedCost(toleratedCost);
        }
    }

    public int getToleratedCost() {
        return toleratedCost;
    }
}

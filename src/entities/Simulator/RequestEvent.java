/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

public class RequestEvent {
    int requestingClientID;
    int requestedFileID;
    float reqTime;

    public RequestEvent(int requestingClientID, int requestedFileID, float reqTime) {
        this.requestingClientID = requestingClientID;
        this.requestedFileID = requestedFileID;
        this.reqTime = reqTime;
    }
}

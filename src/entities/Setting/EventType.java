/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Setting;

public enum EventType {
    //Link Event Types:
    sendData, //The segment must be put in the optionalContent of the event
    dataSent, // Nothing in optional content
    //Other
    sendReq, //Id of the requested file should be put in the optionalContent of the event
    receiveSegment, //sendSegment should be put in optionalContent
    requestServed, //The served request should be put in the optional content
    forward ,
    timeOut //Id of the waiting request should be put in the optionalContent



}

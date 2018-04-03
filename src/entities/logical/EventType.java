package entities.logical;

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

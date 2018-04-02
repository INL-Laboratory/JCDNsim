package entities.logical;

public enum EventType {
    //Link Event Types:
    sendData, dataSent,
    //Other
    sendReq, receiveSegment, requestServed, forward , timeOut
}

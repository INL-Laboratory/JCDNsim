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

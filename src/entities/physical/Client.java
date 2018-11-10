package entities.physical;

import entities.logical.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Client extends EndDevice {
//    public static int generatedId = 0;
// In order to have unique IDs I changed this to static.


    private Link link;

    private final HashMap<Integer, Float> sentRequestsTime = new HashMap<>();   //Maps the sent requests' id to the time they have been sent
    private final HashMap<Integer, Integer> sentRequestsFileId = new HashMap<>();   //Maps the sent requests' id to the file have been request
    private final HashMap<Integer, Float> servedRequestsTime = new HashMap<>(); //Maps the successfully served requests' id to the time the response has been received
    private final HashMap<Integer, Integer> servedRequestsCost = new HashMap<>(); //Maps the successfully served requests' id to the time the response has been received
    private final List<Integer> unansweredRequests = new LinkedList<>();

    public Client(int number, EventsQueue eventsQueue, AlgorithmData algorithmData) {
        super(number, eventsQueue, algorithmData);
    }

    public Link getLink() {
        return link;
    }

    public HashMap<Integer, Float> getSentRequestsTime() {
        return sentRequestsTime;
    }

    public HashMap<Integer, Float> getServedRequestsTime() {
        return servedRequestsTime;
    }

    public HashMap<Integer, Integer> getServedRequestsCost() {
        return servedRequestsCost;
    }

    public void setLink(Link link) {
        this.link = link;
    }


//    public static double totalTimeINClientHandleEvent = 0;


    /**
     * The events involving a client are sent here through below method. Possible course of actions:
     * 1. sendReq: When a client intends to send a request
     * 2. receiveSegment: When a segment was recognized to be associated with this entity
     * 3. timeOut: When the response for a request is not received after a certain time,
     * it will time out and added to the list of not received responses.
     * @param event The related event to this entity
     * @throws Exception
     */
    @Override
    public void handleEvent(Event event) throws Exception {
//        double tempTime = System.currentTimeMillis();
        switch (event.getType()){
            case sendReq:
                sendFileRequest(event.getTime(), (Integer) event.getOptionalData());
                break;
            case receiveSegment:
                receiveData(event.getTime(),(Segment) event.getOptionalData() , (Link)event.getCreator());
                break;
            case timeOut:
                int requestID = (int)event.getOptionalData();
                if (servedRequestsTime.get(requestID)==null){
//                    Logger.print(this + " 's "+ requestID + " remained unanswered ",event.getTime());
                    unansweredRequests.add(requestID);
                }

        }
//        totalTimeINClientHandleEvent +=System.currentTimeMillis()-tempTime;
    }

    /**
     * It sends a request for a file.The file and the time is set by another class and sent in an event package to this client
     * @param time The time the clients want to send
     * @param fileID The desired file
     */
    private void sendFileRequest(float time, int fileID) {
        Server dstServer = (Server)link.getOtherEndPoint(this);
        generateId();
        Request request = new Request(this,dstServer, fileID , algorithmData.generatedId);
        Segment segment = new Segment(
                algorithmData.generatedId, this, dstServer , DefaultValues.REQUEST_SIZE,
                SegmentType.Request,request,0
        );
        sentRequestsTime.put(algorithmData.generatedId,time);
        sentRequestsFileId.put(algorithmData.generatedId,fileID);
//        Logger.print(this + "makes " + request+ " for file " +fileID+" , puts in " + segment,time);
        sendData(time,link,segment);
        if (DefaultValues.IS_TIME_OUT_ACTIVATED) {
            eventsQueue.addEvent(
                    new Event<>(EventType.timeOut, this, time + DefaultValues.TIME_OUT, this, segment.getId())
            );
        }

    }

    private int generateId(){
        algorithmData.generatedId++;
        return algorithmData.generatedId;
    }


    @Override
    protected boolean isReceivedDataValid(Link link) {
        return  link.equals(this.link);
    }


    /**
     * This method override its father in EndDevice.
     * @param time The time segment received
     * @param segment The received segment
     * @throws Exception: When an unexpected packet type is received.
     */
    @Override
    protected void parseReceivedSegment(float time, Segment segment) throws OkayException {
//        Logger.print(segment+ " is being parsed by " + this, time);
        if (isThisDeviceDestined(segment)) {
            switch (segment.getSegmentType()) {
                case Data:
                    checkTheReceivedData(time, segment);
                    break;
                case Request:
                default:
                    throw new OkayException(this + " received unexpected " +segment, time);
            }
        }else{
            throw new OkayException(this + " received " + segment + " whose destination wasn't this client." , time);
        }
    }


    /**
     * It checks the received data and if it's containing a file being waited it will submit the records.
     * @param time
     * @param segment
     * @throws OkayException When the response is received after being timed out
     */
    private void checkTheReceivedData(float time, Segment segment) throws OkayException {
        IFile receivedFile = (IFile) segment.getOptionalContent();
        int requestID = segment.getId();
        int receivedFileID = receivedFile .getId();
        Float sendTime = this.sentRequestsTime.get(requestID);
        Integer requestedFileId = this.sentRequestsFileId.get(requestID);
        if (sendTime==null || requestedFileId==null || requestedFileId != receivedFileID)   throw new OkayException(this + " received unrelated "+ receivedFile+ " in " + segment,time);
        if (DefaultValues.IS_TIME_OUT_ACTIVATED && time-sendTime>= DefaultValues.TIME_OUT)  {
            throw new OkayException(this + "says to file " + requestedFileId +" in " + segment + " : Thanks for coming, but it's late.", time);
        }
        servedRequestsTime.put(requestID,time);
        servedRequestsCost.put(requestID, segment.getToleratedCost());
//        Logger.print(this + " successfully received its file "+ requestedFileId + " in "+ segment + " with delay = " + (time -sendTime) + " tolerated cost = " + segment.getToleratedCost() , time );
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Client{");
        sb.append(number);
        sb.append('}');
        return sb.toString();
    }
}

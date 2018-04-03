package entities.physical;

import entities.logical.*;
import entities.utilities.logger.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Client extends EndDevice {
    private static int generatedId = 0;         //In order to have unique IDs I changed this to static.


    private Link link;

    private final HashMap<Integer, Float> sentRequestsTime = new HashMap<>();   //Maps the sent requests' id to the time they have been sent
    private final HashMap<Integer, Integer> sentRequestsFileId = new HashMap<>();   //Maps the sent requests' id to the file have been request
    private final HashMap<Integer, Float> servedRequests = new HashMap<>(); //Maps the successfully served requests' id to the time the response has been received
    private final List<Integer> unansweredRequests = new LinkedList<>();

    public Client(int number) {
        super(number);
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public void handleEvent(Event event) throws Exception {
        switch (event.getType()){
            case sendReq:
                sendFileRequest(event.getTime(), Integer.parseInt((String)event.getOptionalData()));
                break;
            case receiveSegment:
                receiveData(event.getTime(),(Segment) event.getOptionalData() , (Link)event.getCreator());
                break;
            case timeOut:
                int requestID = (int)event.getOptionalData();
                if (servedRequests.get(requestID)==null){
                    Logger.print(this + " 's "+ requestID + " remained unanswered ",event.getTime());
                    unansweredRequests.add(requestID);
                }

        }
    }

    private void sendFileRequest(float time, int fileID) {
        Server dstServer = (Server)link.getOtherEndPoint(this);
        generateId();
        Request request = new Request(this,dstServer, fileID , generatedId);
        Segment segment = new Segment(
                generatedId, this, dstServer , DefaultValues.REQUEST_SIZE,
                SegmentType.Request,request
        );
        sentRequestsTime.put(generatedId,time);
        sentRequestsFileId.put(generatedId,fileID);
        Logger.print(this + "makes " + request+ " for file " +fileID+" , puts in " + segment,time);
        sendData(time,link,segment);
        EventsQueue.addEvent(
                new Event<>(EventType.timeOut, this, time + DefaultValues.TIME_OUT,this, segment.getId())
        );

    }

    private int generateId(){
        generatedId++;
        return generatedId;
    }


    @Override
    protected boolean isReceivedDataValid(Link link) {
        return  link.equals(this.link);
    }

    @Override
    protected void parseReceivedSegment(float time, Segment segment) throws Exception {
        Logger.print(segment+ " is being parsed by " + this, time);
        if (isThisDeviceDestined(segment)) {
            switch (segment.getSegmentType()) {
                case Data:
                    checkTheReceivedFile(time, segment);
                    break;
                case Request:
                default:
                    throw new OkayException(this + " received unexpected " +segment, time);
            }
        }else{
            throw new OkayException(this + " received " + segment + " whose destination wasn't this client." , time);
        }
    }

    private void checkTheReceivedFile(float time, Segment segment) throws Exception {
        IFile receivedFile = (IFile) segment.getOptionalContent();
        int requestID = segment.getId();
        int receivedFileID = receivedFile .getId();
        Float sendTime = this.sentRequestsTime.get(requestID);
        Integer requestedFileId = this.sentRequestsFileId.get(requestID);
        if (sendTime==null || requestedFileId==null || requestedFileId != receivedFileID)   throw new OkayException(this + " received unrelated "+ receivedFile+ " in " + segment,time);
        if (time-sendTime>= DefaultValues.TIME_OUT)  {
            throw new OkayException(this + "says to file " + requestedFileId +" in " + segment + " : Amadi Janam Beghorbanat Vali hala chera", time);
        }
        servedRequests.put(requestID,time);
        Logger.print(this + " successfully received its file "+ requestedFileId + " in "+ segment + " with delay = " + (time -sendTime) , time );
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Client{");
        sb.append(number);
        sb.append('}');
        return sb.toString();
    }
}

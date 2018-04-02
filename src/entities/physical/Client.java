package entities.physical;

import entities.logical.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Client extends EndDevice {
    private static int generatedId;         //In order to have unique IDs I changed this.
    private Link link;
    private HashMap<Integer, Float> sentRequestsTime = new HashMap<>();   //Maps the sent requests' id to the time they have been sent
    private HashMap<Integer, Integer> sentRequestsFileId = new HashMap<>();   //Maps the sent requests' id to the file have been request
    private HashMap<Integer, Float> servedRequests = new HashMap<>(); //Maps the successfully served requests' id to the time the response has been received
    private List<Integer> unansweredRequests = new LinkedList<>();

    public Client(){
        generatedId = 0;
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
                sendFileRequest(event.getTime(), 0);
                //TODO: Add needed file to optionalContent of segment
                break;
            case receiveSegment:
                receiveData(event.getTime(),(Segment) event.getOptionalData() , (Link)event.getCreator());
                break;
            case timeOut:
                int requestID = (int)event.getOptionalData();
                if (servedRequests.get(requestID)==null){
                    unansweredRequests.add(requestID);
                }

        }
    }

    private void sendFileRequest(float time, int fileID ) {
        Server dstServer = (Server)link.getOtherEndPoint(this);
        generateId();
        Segment segment = new Segment(
                generatedId, this, dstServer , DefaultValues.REQUEST_SIZE,
                SegmentType.Request, new Request(this,dstServer, fileID , generatedId) //todo: this id should be generated
        );
        sentRequestsTime.put(generatedId,time);
        sentRequestsFileId.put(generatedId,fileID);
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
    protected void parseReceivedSegment(float time, Segment segment) {
        if (isThisDeviceDestined(segment)) {
            switch (segment.getSegmentType()) {
                case Data:
                    checkTheReceivedFile(time, segment);
                    break;
                case Request:
                default:
                    throw new RuntimeException("Segment dropped. Unexpected segment received by client" + toString());
            }
        }else{
            throw new RuntimeException("Segment dropped. File with wrong destination received file received by client");
        }
    }

    private void checkTheReceivedFile(float time, Segment segment) {
        IFile receivedFile = (IFile) segment.getOptionalContent();
        int requestID = segment.getId();
        int receivedFileID = receivedFile .getId();
        Float sendTime = this.sentRequestsTime.get(requestID);
        Integer requestedFileId = this.sentRequestsFileId.get(requestID);
        if (sendTime==null || requestedFileId==null || requestedFileId != receivedFileID)   throw new RuntimeException("Unrelated File received by client.");
        if (time-sendTime>= DefaultValues.TIME_OUT)  throw new RuntimeException("Amadi Janam Beghorbanat Vali hala chera, client.");
        servedRequests.put(requestID,time);
    }


}

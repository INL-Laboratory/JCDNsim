package entities.physical;

import entities.logical.*;

import java.util.*;

public class Server extends EndDevice{
    private Map<Server,Link> serverslinks = new HashMap<>();
    private Link clientLink;
    private EndDevice client;
    private List<IFile> files;
    private int cacheSize = DefaultValues.CACHE_SIZE;
    private Map<EndDevice, Link> routingTable;
    Queue<Request> queue = new ArrayDeque<>();

    public List<IFile> getFiles() {
        return files;
    }

    public void setFiles(List<IFile> files) {
        this.files = files;
    }

    public Map<Server, Link> getServerslinks() {
        return serverslinks;
    }

    public void setServerslinks(Map<Server, Link> serverslinks) {
        this.serverslinks = serverslinks;
    }

    public Link getClientLink() {
        return clientLink;
    }

    public void setClientLink(Link clientLink) {
        this.clientLink = clientLink;
    }

    public boolean receiveData(Event event){
        if(!isRecievedDataValid((Link)event.getCreator()))
            return false;
        parseReceivedSegment(event);
        return true;
    }

    private boolean isThisServerDestined(Segment optionalData) {
        return optionalData.getDestination().equals(this);
    }

    private boolean isRecievedDataValid(Link link) {
        /***
         * Checks the validity of the link from the segment arrived.
         */
        boolean linkExistence = clientLink.equals(link) || serverslinks.values().contains(link) ;
        return linkExistence;
    }

    private void parseReceivedSegment(Event event) {
        /***
         * takes suitable course of action according to the type of the segment
         */
        Segment segment = (Segment) event.getOptionalData();
        if (isThisServerDestined(segment)) {
            switch (segment.getSegmentType()) {

                case Request:
                    Request request = ((Segment) event.getOptionalData()).getOptionalContent();
                    queue.add(request);
                    if (queue.size()==1) {
                        setTimerToPopQueue(event.getTime());
                    }
                    break;
                case Data:
                default:
                    throw new RuntimeException("Segment dropped. Unexpected file received by server" + toString());
            }
        }else{
            forwardSegment(event.getTime(), segment);
        }

    }

    private void setTimerToPopQueue(float time) {
        /***
         * releases an event which at current time + service time pops the queue
         */
            if (queue.size()==0) return;
            EventsQueue.addEvent(
                    new Event<>(EventType.pop, this, time + DefaultValues.SERVICE_TIME, this)
            );

    }

    private void forwardSegment(float time, Segment segment) {
        /***
         * Forwards the segment to the intended server using routing table and the corresponding link
         */
        EndDevice dest = segment.getDestination();
        Link link = routingTable.get(dest);
        sendData(time, link , segment);      //Without any delay forward the packet
    }


    private void serveRequest(float time, Request request) {
        /***
         * Checks if the file is cached. If Yes sends the file, otherwise finds another server.
         */
        IFile neededFile= findFile(request.getNeededFileID());
        if (neededFile == null){
            forwardToSuitableServer();
        }

        EndDevice destination = request.getSource();
        Link link = routingTable.get(destination);
        Segment fileSegment = new Segment(request.getId(), this, destination , neededFile.getSize() , SegmentType.Data);
        sendData(time, link, fileSegment);
    }

    private void sendData(float time, Link link, Segment segment) {
        EventsQueue.addEvent(
                new Event<>(EventType.sendData, link, time, this , segment)
        );
    }

    private void forwardToSuitableServer() {
        //TODO: find a suitable server from graph to respond to the request
    }

    private IFile findFile(int fileID){
        /***
        * searches for a file in cached files with corresponding fileID */
        for (IFile f:files) {
            if (f.getId()==fileID){
                return f;
            }
        }
        return null;
    }

    @Override
    public void handleEvent(Event event) throws Exception {
        switch (event.getType()){
            case receiveSegment:
                receiveData(event);
                break;
            case pop:
                pop(event.getTime());
        }
    }

    private void pop(float time) {
        /***
         * Commands to serve the request then after a service delay serve the next request
         */
        if (queue.size()==0) return;
        Request request = queue.remove(); //popping action
        serveRequest(time, request);
        setTimerToPopQueue(time);
    }

    public Map<EndDevice, Link> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(Map<EndDevice, Link> routingTable) {
        this.routingTable = routingTable;
    }
}

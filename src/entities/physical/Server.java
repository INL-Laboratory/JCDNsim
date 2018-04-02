package entities.physical;

import entities.logical.*;
import entities.utilities.logger.Logger;

import java.util.*;

public class Server extends EndDevice{
    private Map<Server,Link> links = new HashMap<>();
//    private Link clientLink;
    private List<IFile> files;
    private int cacheSize = DefaultValues.CACHE_SIZE;
    private Map<EndDevice, Link> routingTable = new HashMap<>();
    private Map<EndDevice, Integer> communicationCostTable = new HashMap<>();
    private Queue<Request> queue = new ArrayDeque<>();
    private boolean isServerBusy;


    @Override
    protected boolean isReceivedDataValid(Link link) {
        /***
         * Checks the validity of the link from the segment arrived.
         */
        boolean linkExistence =  links.values().contains(link) ;
        return linkExistence;
    }
    @Override
    protected void parseReceivedSegment(float time, Segment segment) throws Exception {
        /***
         * takes suitable course of action according to the type of the segment
         */
        Logger.print(segment+ " is being parsed by " + this, time);
        if (isThisDeviceDestined(segment)) {
            Logger.print(segment+ " is destined for " + this, time);
            switch (segment.getSegmentType()) {
                case Request:
                    Request request = (Request) segment.getOptionalContent();
                    if (queue.size()==0 && !this.isServerBusy)  {
                        Logger.print(this + " wasn't busy and goes to serve " + request,time);
                        serveRequest(time,request);
                    }else {
                        queue.add(request);
                        Logger.print(this + " added to queue: " + request + " queueSize = " + queue.size(),time);
                    }
                    break;
                case Data:
                default:
                    throw new Exception(this + " received unexpected " +segment);
            }
        }else{
            Logger.print(segment+ " is not destined for " + this + " and it will be forwarded", time);
            forwardSegment(time, segment );
        }

    }

    private void setTimeToPopNextEventInQueue(float time , float delay, Request request) {
        /***
         * releases an event which at current time + service time pops the queue
         */
            if (queue.size()==0) return;
            EventsQueue.addEvent(
                    new Event<>(EventType.requestServed, this, time+delay, this, request)
            );

    }

    private void redirectRequest(float time, Request request, Server selectedServer) {
        /***
         * Forwards the request to the intended server  - makes new request and segment
         */

        Client client = request.getSource();
        Request newRequest = new Request(client,selectedServer,request.getNeededFileID(),request.getId());
        newRequest.setRedirect(true);
        Segment newSegment = new Segment(newRequest.getId(),this,selectedServer,DefaultValues.REQUEST_SIZE,SegmentType.Request,newRequest);
        Logger.print(this+ " redirects "+ request +" to " + selectedServer,time);
        forwardSegment(time, newSegment);
    }

    private void forwardSegment(float time, Segment segment) {
        /***
         * Forwards the segment to the intended server using routing table and the corresponding link
         */
        EndDevice destination = segment.getDestination();
        Link link = routingTable.get(destination);
        sendData(time, link , segment);      //Without any delay forward the packet
    }


    private void serveRequest(float time, Request request) throws Exception{
        /***
         */
        if (request.isRedirected()){
            Logger.print(this+ " directly serves the redirected" + request,time);
            sendFile(time, request, 0);
        }
        isServerBusy = true;
        float queryDelay = 0f; //TODO : update this
        float delay = 0f;
        Logger.print(this+ " is looking for a suitable server to serve " + request,time);
        Server selectedServer = getSuitableServer(request);
        if (selectedServer==null) throw new Exception("At "+this+" no server was selected to serve "+request);
        Logger.print(this+ " selected "+ selectedServer + " to serve " + request,time);
        if (selectedServer.equals(this)){
            sendFile(time, request, queryDelay);

        }else {

            delay = queryDelay;
            redirectRequest(time + delay, request, selectedServer);
            setTimeToPopNextEventInQueue(time, delay, request);
        }

    }

    private void sendFile(float time, Request request, float queryDelay) {
        float delay;
        Logger.print(this + "starts to serve the " + request, time);
        EndDevice destination = request.getSource();
        Link link = routingTable.get(destination);
        IFile neededFile= findFile(request.getNeededFileID());
        Segment fileSegment = new Segment(request.getId(), this, destination , neededFile.getSize() , SegmentType.Data, neededFile);
        delay = DefaultValues.SERVICE_TIME + queryDelay;
        Logger.print(this + " puts file " + neededFile + " in " + fileSegment,time );
        sendData(time + delay, link, fileSegment);
    }

    public int getServerLoad(){
        return queue.size();
    }

    private Server getSuitableServer( Request request) throws Exception{
        /***
         *   finds a suitable server from graph to respond to the request
         */
        int fileId = request.getNeededFileID();
        Client client = request.getSource();
        List<Server> serversHavingFile = SimulationParameters.serversHavingFile.get(fileId);
        if (serversHavingFile==null || serversHavingFile.size()==0) throw new Exception(" No server has the file " + fileId + " requested in " + request   );
        Server selectedServer = RedirectingAlgorithm.selectServerToRedirect(SimulationParameters.redirectingAlgorithmType,serversHavingFile,client);
        return selectedServer;
    }



    public IFile findFile(int fileID){
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
                receiveData(event.getTime(),(Segment) event.getOptionalData() , (Link)event.getCreator());
                break;
            case requestServed:
                requestServed(event.getTime(), (Request)event.getOptionalData());
        }
    }

    private void requestServed(float time, Request servedRequest) throws Exception {
        /***
         * Commands to serve the request then after a service delay serve the next request
         */
        if (queue.size()==0) {
            isServerBusy = false;
            Logger.print(this + " has served the request " + servedRequest + " and is not busy now", time);
            return;
        }
        Request nextRequest = queue.remove(); //popping action
        Logger.print(this + " has served the request " + servedRequest + " and goes to serve " + nextRequest  , time);
        serveRequest(time, nextRequest);
    }

    public Map<EndDevice, Link> getRoutingTable() {
        return routingTable;
    }

    public Map<EndDevice, Integer> getCommunicationCostTable() {
        return communicationCostTable;
    }


    public List<IFile> getFiles() {
        return files;
    }

    public void setFiles(List<IFile> files) {
        this.files = files;
    }

    public Map<Server, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<Server, Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Server{");
        sb.append("id=").append(number);
        sb.append('}');
        return sb.toString();
    }

}

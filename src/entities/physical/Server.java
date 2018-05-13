package entities.physical;

import entities.logical.*;
import entities.utilities.logger.Logger;

import java.util.*;

public class Server extends EndDevice{
    private Map<EndDevice,Link> links = new HashMap<>();
//    private Link clientLink;
    private List<IFile> files;
    private final Map<EndDevice, Link> routingTable = new HashMap<>();
    private final  Map<EndDevice, Integer> communicationCostTable = new HashMap<>();
    private final Queue<Request> queue = new ArrayDeque<>();
    private boolean isServerBusy;
    private Map<Integer, List<Server>> serversHavingFile = new HashMap<>();

    public Server(int number, List<IFile> files, Map<Integer, List<Server>> serversHavingFile) {
        super(number);
        this.files = files;
        this.serversHavingFile = serversHavingFile;
    }


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
        Logger.print(this+ " is parsing "+ segment, time);
        if (isThisDeviceDestined(segment)) {
            Logger.print(this + " is the destination of " + segment, time);
            lookAtContent(time, segment);
        }else{
            Logger.print(this + " is not the destination of " + segment + " and it will be forwarded", time);
            forwardSegment(time, segment );
        }

    }

    private void lookAtContent(float time, Segment segment) throws Exception {
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
                throw new OkayException(this + " received unexpected " +segment,time);
        }
    }

    private void setTimeToPopNextRequestInQueue(float time , float delay, Request request) {
        /***
         * releases an event which at current time + service time pops the queue
         */
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
        Segment newSegment = new Segment(newRequest.getId(),this,selectedServer,DefaultValues.REQUEST_SIZE,SegmentType.Request,newRequest,request.getToleratedCost());
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


    public void serveRequest(float time, Request request) throws Exception{
        /***
         */
        float delay = 0f;
        if (request.isRedirected()){
            Logger.print(this+ " directly serves the redirected" + request,time);
            sendFile(time, request, 0);
            delay = DefaultValues.SERVICE_TIME;
        }else {     //if the request is not redirected
            delay = serveUnredirectedRequest(time, request);
        }
        setTimeToPopNextRequestInQueue(time, delay, request);

    }

    private float serveUnredirectedRequest(float time, Request request) throws Exception {
        float delay;
        float queryDelay = 0f; //TODO : update this
        Logger.print(this + " is looking for a suitable server to serve " + request, time);
        Server selectedServer = getSuitableServer(request);
        if (selectedServer == null)
            throw new OkayException("At " + this + " no server was selected to serve " + request , time);
        Logger.print(this + " selected " + selectedServer + " to serve " + request, time);
        if (selectedServer.equals(this)) {              //If this server is selected
            sendFile(time, request, queryDelay);
            delay = DefaultValues.SERVICE_TIME + queryDelay;

        } else {                //if another server is selected

            delay = queryDelay;
            redirectRequest(time + delay, request, selectedServer);
        }
        return delay;
    }

    private void sendFile(float time, Request request, float queryDelay) throws Exception {
        isServerBusy = true;
        float delay;
        Logger.print(this + "starts to serve the " + request, time);
        EndDevice destination = request.getSource();
        Link link = routingTable.get(destination);
        IFile neededFile= findFile(request.getNeededFileID());
        if (neededFile== null) throw new OkayException(this+ "doesn't have the requested file"+request.getNeededFileID(), time);
        Segment fileSegment = new Segment(request.getId(), this, destination , neededFile.getSize() , SegmentType.Data, neededFile, request.getToleratedCost());
        delay = DefaultValues.SERVICE_TIME + queryDelay;
        Logger.print(this + " puts file " + neededFile + " in " + fileSegment,time + delay );
        sendData(time + delay, link, fileSegment);
    }

    public int getServerLoad(){
        return queue.size();
    }


    public Server getSuitableServer( Request request) throws Exception {
        return getSuitableServer(request,0f);
    }

        public Server getSuitableServer( Request request , float time) throws Exception{
        /***
         *   finds a suitable server from graph to respond to the request
         */
        int fileId = request.getNeededFileID();
        Client client = request.getSource();
        List<Server> serversHavingSpecificFile = serversHavingFile.get(fileId);
        if (serversHavingSpecificFile==null || serversHavingSpecificFile.size()==0) throw new OkayException(" No server has the file " + fileId + " requested in " + request , time);
        Server selectedServer = RedirectingAlgorithm.selectServerToRedirect(SimulationParameters.redirectingAlgorithmType,serversHavingSpecificFile,client);
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
        isServerBusy = false;
        if (queue.size()==0) {
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

    public Map<EndDevice, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<EndDevice, Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Server{");
        sb.append(number);
        sb.append('}');
        return sb.toString();
    }
    public String toStringRoutingTable(){
        StringBuffer sb = new StringBuffer();
        sb.append("\n ******** Routing Table of ").append(this).append("******** \n");
        for (EndDevice end:routingTable.keySet()) {
            sb.append(end).append(" : ").append(routingTable.get(end)).append(" cost: ").append(communicationCostTable.get(end)).append("\n");
        }
        return sb.toString();
    }
}

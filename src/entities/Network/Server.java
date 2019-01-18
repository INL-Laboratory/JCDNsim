/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

import entities.Algortihms.RedirectingAlgorithm;
import entities.Setting.AlgorithmData;
import entities.Setting.DefaultValues;
import entities.Setting.EventType;
import entities.Setting.SegmentType;
import entities.Simulator.*;

import java.util.*;

import static entities.Setting.UpdateType.*;

public class Server extends EndDevice implements HasLoadAndCost{
    private Map<EndDevice,Link> links = new HashMap<>();
//    private Link clientLink;
    private List<IFile> files;
    private final Map<EndDevice, Link> routingTable = new HashMap<>();
    private final  Map<EndDevice, Integer> communicationCostTable = new HashMap<>();
    private final Queue<Request> queue = new ArrayDeque<>();
    private boolean isServerBusy;
    private Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
    private Map<Server, Integer> serverLoads = new HashMap<>();
    private Map<Integer, Map<Site,List<Server>>> sitesHavingFile = new HashMap<>();
    private Map<Server, Integer> serverShares = new HashMap<>();
    private Site site ;
    private RedirectingAlgorithm redirectingAlgorithm;




    public Server(int number, List<IFile> files, Map<Integer, List<Server>> serversHavingFile , EventsQueue eventsQueue, AlgorithmData algorithmData, RedirectingAlgorithm redirectingAlgorithm) {
        this(number, eventsQueue, algorithmData, redirectingAlgorithm);
        this.files = files;
        this.serversHavingFile = serversHavingFile;
    }
    public Server(int number, EventsQueue eventsQueue, AlgorithmData algorithmData,RedirectingAlgorithm redirectingAlgorithm) {
        super(number,eventsQueue,algorithmData);
        this.redirectingAlgorithm = redirectingAlgorithm;

    }


    /***
         * Checks the validity of the link from the segment arrived.
     */
    @Override
    protected boolean isReceivedDataValid(Link link) {
        boolean linkExistence =  links.values().contains(link) ;
        return linkExistence;
    }
        /***
         * takes suitable course of action according to the type of the segment
         */
    @Override
    protected void parseReceivedSegment(float time, Segment segment) throws Exception {
//            Logger.print(this+ " is parsing "+ segment, time);
        if (isThisDeviceDestined(segment)) {
//            Logger.print(this + " is the destination of " + segment, time);
            lookAtContent(time, segment);
        }else{
//            Logger.print(this + " is not the destination of " + segment + " and it will be forwarded", time);
            forwardSegment(time, segment );
        }

    }
//    public static int maxQueue = 0;
    /***
     * checks the content of the packet and spots its type
     */
    private void lookAtContent(float time, Segment segment) throws OkayException {
        switch (segment.getSegmentType()) {
            case Request:
                Request request = (Request) segment.getOptionalContent();
                if (queue.size()==0 && !this.isServerBusy)  {
//                    Logger.print(this + " wasn't busy and goes to serve " + request,time);
//                    System.out.println(this+" received request at "+ time);
                    serveRequest(time,request);
                }else {
                    queue.add(request);
//                    if (queue.size()>maxQueue) maxQueue = queue.size();
//
//                    Logger.print(this + " added to queue: " + request + " queueSize = " + queue.size(),time);
//
                }
                break;
            case Data:
                break;
            case Update:
//
//                Logger.print(this + "update package from "+ segment.getSource() +" received ",time);
//
                updateLoadList((List<LoadPair>) segment.getOptionalContent());
                break;
            default:
                throw new OkayException(this + " received unexpected " +segment,time);
        }
    }

    /***
     * It receives a list of pairs in which there is a server and it's load. This information is used to update the way the load list in the server.
     */
    private void updateLoadList(List<LoadPair> loads) {
        for(LoadPair load: loads){
            serverLoads.put(load.server,load.load);
        }
        serverLoads.put(this,getServerLoad());
//
//        Logger.printWithoutTime("******"+this + "'s load list");
//
//        for (Server s:serverLoads.keySet()) {
//            Logger.printWithoutTime("      "+s + " : " + serverLoads.get(s));
//        }
    }

    /***
       * releases an event at current time + service time pops the queue
     */
    private void setTimeToPopNextRequestInQueue(float time , float delay, Request request) {
            eventsQueue.addEvent(
                    new Event<>(EventType.requestServed, this, (time+delay), this, request)
            );

    }

        /***
         * Forwards the request to the intended server  - makes new request and segment
         */
    private void redirectRequest(float time, Request request, Server selectedServer) {

        Client client = request.getSource();
        Request newRequest = new Request(client,selectedServer,request.getNeededFileID(),request.getId());
        newRequest.setServerToPiggyBack(this);
        newRequest.setRedirect(true);
        Segment newSegment = new Segment(newRequest.getId(),this,selectedServer,DefaultValues.REQUEST_SIZE,SegmentType.Request,newRequest,request.getToleratedCost());
//
//        Logger.print(this+ " redirects "+ request +" to " + selectedServer,time);
//
        forwardSegment(time, newSegment);
    }

    /***
         * Forwards the segment to the intended server using routing table and the corresponding link
     */
    private void forwardSegment(float time, Segment segment) {
        EndDevice destination = segment.getDestination();
        Link link = routingTable.get(destination);
        sendData(time, link , segment);      //Without any delay forward the packet
    }


    public void serveRequest(float time, Request request) throws OkayException{
        float delay = 0f;
        if (request.isRedirected()){
//
//            Logger.print(this+ " directly serves the redirected" + request,time);
//
            sendFile(time, request, 0);
            delay = DefaultValues.SERVICE_TIME;
            request.setShouldBePiggiedBack(true);
        }else {     //if the request is not redirected
            delay = serveUnredirectedRequest(time, request);
        }
        setTimeToPopNextRequestInQueue(time, delay, request);
    }

    private void piggyBack(float time, Request request , boolean sendSiteUpdate) {
        Server requestingServer = request.getServerToPiggyBack();
        sendUpdateTo(time, request.getId(), requestingServer , sendSiteUpdate);
//        System.out.println(this+ " : sends piggy back at "+ time);

    }

    private void sendUpdateTo(float time, int  id, Server dst, boolean sendSiteUpdate) {
        Link link = routingTable.get(dst);
//        HashMap<Server, Integer> updateHashMap= new HashMap<>();
//        if (sendSiteUpdate) {
//            updateHashMap.putAll(site.getRealLoads());
//        }else {
//            updateHashMap.put(this,getServerLoad());
//        }
        List<LoadPair> pairs = new ArrayList<>();
        if (sendSiteUpdate) {
            for (Server server : site.getServers()) {
                LoadPair pair =new LoadPair(server,serverLoads.get(server));
                pairs.add(pair);
            }
            Segment updateSegment = new Segment(id,this, dst , DefaultValues.PIGGY_BACK_SIZE, SegmentType.Update,pairs , 0);
            sendData(time, link, updateSegment);

        }else {
            LoadPair pair =new LoadPair(this,getServerLoad());
            pairs.add(pair);
            Segment updateSegment = new Segment(id,this, dst , DefaultValues.PIGGY_BACK_SIZE, SegmentType.Update,pairs , 0);
            sendData(time, link, updateSegment);
        }
    }

    private float serveUnredirectedRequest(float time, Request request ) throws OkayException {
        float delay;
        float queryDelay = 0f; //TODO : update this
//
//        Logger.print(this + " is looking for a suitable server to serve " + request, time);
//
        Server selectedServer = getSuitableServer(request);
        if (selectedServer == null)
            throw new OkayException("At " + this + " no server was selected to serve " + request , time);
//
//        Logger.print(this + " selected " + selectedServer + " to serve " + request, time);
//
        if (selectedServer.equals(this)) {              //If this server is selected
            sendFile(time, request, queryDelay);
            delay = DefaultValues.SERVICE_TIME + queryDelay;

        } else {                //if another server is selected

            delay = queryDelay;
            redirectRequest(time + delay, request, selectedServer);
        }
        return delay;
    }

    private void sendFile(float time, Request request, float queryDelay) throws OkayException {
        isServerBusy = true;
        float delay;
//        Logger.print(this + "starts to serve the " + request, time);
        EndDevice destination = request.getSource();
        Link link = ((Client) destination).getLink();
        IFile neededFile= findFile(request.getNeededFileID());
        if (neededFile== null) throw new OkayException(this+ "doesn't have the requested file"+request.getNeededFileID(), time);
        Segment fileSegment = new Segment(request.getId(), this, destination , neededFile.getSize() , SegmentType.Data, neededFile, request.getToleratedCost());
        delay = DefaultValues.SERVICE_TIME+100;
//
//        Logger.print(this + " puts file " + neededFile + " in " + fileSegment + " at " + (time + delay),time  );
//
        //        System.out.println(this+ " will send file to "+ destination +" at "+time+delay);
        eventsQueue.addEvent(
                new Event<>(EventType.sendData, link, time+delay, link.getEndPointB() , fileSegment)
        );
    }

    int getServerLoad(){
        return queue.size();
    }


    public Server getSuitableServer( Request request) throws OkayException {
        return getSuitableServer(request,0f);
    }


    /***
         *   finds a suitable server from graph to respond to the request
     */
    public Server getSuitableServer( Request request , float time) throws OkayException{
        int fileId = request.getNeededFileID();
        Client client = request.getSource();
        List<Server> serversHavingSpecificFile = serversHavingFile.get(fileId);
        Map<Site,List<Server>> sitesHavingSpecificFile = sitesHavingFile.get(fileId);
        if (serversHavingSpecificFile==null || serversHavingSpecificFile.size()==0) throw new OkayException(" No server has the file " + fileId + " requested in " + request , time);
        if (sitesHavingSpecificFile==null || sitesHavingSpecificFile.size()==0) throw new OkayException(" No site has the file " + fileId + " requested in " + request , time);
        if (algorithmData.updateType==ideal)
              makeLoadListIdeally(serversHavingSpecificFile,serverLoads);
        serverLoads.put(this,getServerLoad());
        setShares();
        Server selectedServer = redirectingAlgorithm.selectServerToRedirect(algorithmData.redirectingAlgorithmType,serversHavingSpecificFile,serverLoads,client, serverShares,sitesHavingSpecificFile);
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
//    public static double totalTimeInServerHandleEvent = 0;
    @Override
    public void handleEvent(Event event) throws Exception {
//        double tempTime = System.currentTimeMillis();
        switch (event.getType()){
            case receiveSegment:
                receiveData(event.getTime(),(Segment) event.getOptionalData() , (Link)event.getCreator());
                break;
            case requestServed:
                requestServed(event.getTime(), (Request)event.getOptionalData());
        }
//        totalTimeInServerHandleEvent += System.currentTimeMillis()-tempTime;

    }

    private void requestServed(float time, Request servedRequest) throws Exception {
        /***
         * Commands to serve the request then after a service delay serve the next request
         */
        if (servedRequest.getShouldBePiggiedBack() ) {
            if(algorithmData.updateType == piggyBack || algorithmData.updateType == piggyGroupedPeriodic)
                piggyBack(time, servedRequest , piggyGroupedPeriodic == algorithmData.updateType);
        }
        isServerBusy = false;
        if (queue.size()==0) {
//
//            Logger.print(this + " has served the request " + servedRequest + " and is not busy now", time);
//
            return;
        }
        Request nextRequest = queue.remove(); //popping action
//
//        Logger.print(this + " has served the request " + servedRequest + " and goes to serve " + nextRequest  , time);
//
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
//    public static double totalTimeInMakeLoadListIdeally = 0;
    public static int makeLoadListIdeally(List<Server> serversHavingSpecificFile, Map<Server, Integer> serverLoads){
//        double tempTime = System.currentTimeMillis();
        int sum = 0;
        for (Server server:
             serversHavingSpecificFile) {
            serverLoads.put(server,server.getServerLoad());
            sum+= server.getServerLoad();

        }
//        totalTimeInMakeLoadListIdeally += System.currentTimeMillis() - tempTime;
        return sum;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setServersHavingFile(Map<Integer, List<Server>> serversHavingFile) {
        this.serversHavingFile = serversHavingFile;
    }


    public Map<Server, Integer> getServerLoadListss() {
        return serverLoads;
    }

    public void setServerLoadListss(Map<Server, Integer> serverLoads) {
        this.serverLoads = serverLoads;
    }

    public void sendUpdateToAll(float time,List<Server> servers) {
        algorithmData.generatedId++;
        int id = algorithmData.generatedId;
        for (Server dst:servers) {
            if (dst.equals(this)) continue;
            sendUpdateTo(time,id,dst, false);
        }

    }

    public void setShares() {
        for(Server server:serverLoads.keySet()) {
            serverShares.put(server,serverLoads.get(server)-this.getServerLoad());
        }
    }

    public EndDevice getClient(){
        for(EndDevice item : getLinks().keySet()){
            if(item instanceof Client)
                return item;
        }
        return null;
    }

    public void setSitesHavingFile(Map<Integer, Map<Site, List<Server>>> sitesHavingFile) {
        this.sitesHavingFile = sitesHavingFile;
    }
}


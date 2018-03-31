package entities.physical;

import entities.logical.*;

import java.util.List;

public class Server extends EndDevice{
    private List<Link> serverslinks;
    private Link clientLink;
    private List<IFile> files;
    private int cacheSize = DefaultValues.CACHE_SIZE;

    public List<IFile> getFiles() {
        return files;
    }

    public void setFiles(List<IFile> files) {
        this.files = files;
    }

    public List<Link> getServerslinks() {
        return serverslinks;
    }

    public void setServerslinks(List<Link> serverslinks) {
        this.serverslinks = serverslinks;
    }

    public Link getClientLink() {
        return clientLink;
    }

    public void setClientLink(Link clientLink) {
        this.clientLink = clientLink;
    }

    public boolean receiveData(Event event){
        if(!isRecievedDataValid((Link)event.getCreator(), (Segment)event.getOptionalData()))
            return false;
        parseReceivedSegment(event);
        return true;
    }

    private boolean isRecievedDataValid(Link link, Segment segment) {
        /***
         * Checks the validity of the link from the segment arrived. and the destination validity
         */
        boolean linkExistence = clientLink.equals(link) || serverslinks.contains(link) ;
        boolean correctDest = this.equals(segment.getDestination());
        return correctDest & linkExistence;
    }

    private void parseReceivedSegment(Event event) {
        /***
         * takes suitable course of action according to the type of the segment
         */
        Segment segment = (Segment) event.getOptionalData();
        Link link =(Link) event.getCreator();
        switch (segment.getSegmentType()){
            case Data:

                break;
            case Request:
                answerRequest(event, segment, link);
                break;
        }


    }

    private void answerRequest(Event event, Segment segment, Link link) {
        /***
         * Checks if the file is cached. If Yes sends the file, otherwise find another server.
         */
        IFile neededFile= findFile(segment.getOptionalContent().getNeededFileID());
        if (neededFile == null){
            forwardToSuitableServer();
        }
        EndDevice destination = link.getOtherEndPoint(this);
        Segment fileSegment = new Segment(segment.getId(), this, destination , neededFile.getSize() , SegmentType.Data);
        EventsQueue.addEvent(
                new Event<>(EventType.sendData, link, event.getTime(), this , fileSegment)
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
        }
    }
}

package entities.logical;

import entities.physical.Client;
import entities.physical.Server;

public class Request {
    private Client source;
    private Server destination;
    private int neededFileID;
    private boolean isRedirect = false;

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }

    public int getNeededFileID() {
        return neededFileID;
    }

    public Request(Client source, Server destination, int neededFileID) {
        this.source = source;
        this.destination = destination;
        this.neededFileID = neededFileID;
    }

    public void setNeededFileID(int neededFileID) {
        this.neededFileID = neededFileID;
    }

    public Client getSource() {
        return source;
    }

    public void setSource(Client source) {
        this.source = source;
    }

    public Server getDestination() {
        return destination;
    }

    public void setDestination(Server destination) {
        this.destination = destination;
    }
}

package entities.logical;

import entities.physical.Client;
import entities.physical.Server;

public class Request {
    private Client source;
    private Server destination;
    private int neededFileID;
    private final int id;     //This must be the same id in segment
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

    public Request(Client source, Server destination, int neededFileID , int id) {
        this.source = source;
        this.destination = destination;
        this.neededFileID = neededFileID;
        this.id = id;
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Request{");
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}

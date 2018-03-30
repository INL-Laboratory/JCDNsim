package entities.logical;

import entities.physical.Client;
import entities.physical.Server;

public class Request {
    private Client source;
    private Server destination;
    private IFile neededFile;
    private boolean isRedirect;

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }

    public IFile getNeededFile() {
        return neededFile;
    }

    public void setNeededFile(IFile neededFile) {
        this.neededFile = neededFile;
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

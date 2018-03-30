package entities.physical;

import entities.logical.Event;
import entities.logical.IFile;

import java.util.List;

public class Server extends EndDevice{
    private List<Link> serverslinks;
    private Link clientLink;
    private List<IFile> files;

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

    @Override
    public void handleEvent(Event event) throws Exception {

    }
}

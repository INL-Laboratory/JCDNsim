package entities.physical;

import entities.logical.Event;

public class Client extends EndDevice {
    private Link link;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public void handleEvent(Event event) throws Exception {

    }
}

package entities.physical;

import entities.physical.Server;

public class loadPair{
    Server server;
    int load;

    public loadPair(Server server, int load) {
        this.server = server;
        this.load = load;
    }
}

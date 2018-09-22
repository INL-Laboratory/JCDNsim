package entities.physical;

import entities.physical.Server;

public class LoadPair {
    Server server;
    int load;

    public LoadPair(Server server, int load) {
        this.server = server;
        this.load = load;
    }
}

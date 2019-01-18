package entities.Network;

public class LoadPair {
    Server server;
    int load;

    public LoadPair(Server server, int load) {
        this.server = server;
        this.load = load;
    }
}

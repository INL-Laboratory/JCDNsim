package entities.physical;

import entities.physical.Server;

import java.util.ArrayList;
import java.util.HashMap;


public class Site {
    private ArrayList<Server> servers = new ArrayList<>();
    private HashMap<Server, Integer> loads = new HashMap<>();
    int id ;

    public int getId() {
        return id;
    }


    public Site(int id) {
        this.id = id;
    }

    public ArrayList<Server> getServers() {
        return servers;
    }


    public HashMap<Server, Integer> getLoads() {
        for (int i = 0; i < servers.size() ; i++) {
            loads.put(servers.get(i),servers.get(i).getServerLoad());
        }
        return loads;
    }

    public void addServer(Server server){
        servers.add(server);
        server.setSite(this);
    }
}

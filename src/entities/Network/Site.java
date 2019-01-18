package entities.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Site implements HasLoadAndCost{
    private ArrayList<Server> servers = new ArrayList<>();
    int id ;

    private final Map<EndDevice, Integer> communicationCostTable = new HashMap<>();





    public int getId() {
        return id;
    }


    public Site(int id) {
        this.id = id;
    }

    public ArrayList<Server> getServers() {
        return servers;
    }


    public HashMap<Server, Integer> getRealLoads() {
        HashMap<Server, Integer> loads = new HashMap<>();

        for (int i = 0; i < servers.size() ; i++) {
            loads.put(servers.get(i),servers.get(i).getServerLoad());
        }
        return loads;
    }

    public void addServer(Server server){
        servers.add(server);
        server.setSite(this);
    }

    public void makeRoutingTable(){
        try {
            if (servers == null || servers.size() == 0) return;
            Set<EndDevice> sources = servers.get(0).getCommunicationCostTable().keySet();
            for (EndDevice src : sources) {
                int sum = 0;
                for (Server server : servers) {
                    if (server.equals(src)) continue;
                    sum += server.getCommunicationCostTable().get(src);
                }
                communicationCostTable.put(src, sum);

            }

            for (Server server: servers){
                communicationCostTable.put(server, 0);
                assert server.getClient()!= null;
                communicationCostTable.put(server.getClient(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Map<EndDevice, Integer> getCommunicationCostTable() {
        return communicationCostTable;
    }
}

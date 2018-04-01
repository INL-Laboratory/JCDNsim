package entities.physical;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hd on 2018/4/1 AD.
 */
public class NetworkGraph extends UndirectedSparseGraph<EndDevice,Link> {
    public void buildRoutingTables(){
        /***
         * Completely builds routing tables of all servers using Dijkestra shortest path algorithm
         */
        Server src;
        for (EndDevice end:getVertices()) {
            if (!(end instanceof Server)) continue;
            src= (Server) end;
            DijkstraShortestPath<EndDevice,Link> algorithm =
                    new DijkstraShortestPath<EndDevice, Link>(
                            this, link -> link.getWeight()
                    );
            Server dest;
            List< Link> path;
            Link link;
            for (EndDevice endd:getVertices()) {
                dest = (Server)endd;
                if (dest==src) continue;
                if (src.getRoutingTable().get(dest)!= null) continue;
                path = algorithm.getPath(src,dest);
                link = path.get(0);
                src.getRoutingTable().put(dest,link);
                //TODO: We should optimize this part if it was slow by exploiting the path list. Now we just use the first element of that
                //TODO: We should test whether the first link is at index 0 or the last one
            }
        }
    }
}

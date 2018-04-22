package entities.physical

import edu.uci.ics.jung.graph.util.EdgeType
import entities.logical.Brain
import entities.logical.Event
import entities.logical.EventType
import entities.logical.EventsQueue
import entities.logical.IFile
import entities.logical.RedirectingAlgorithmType
import entities.logical.Request
import entities.logical.Segment
import entities.logical.SegmentType
import entities.logical.SimulationParameters
import entities.utilities.logger.Logger

/**
 * Created by hd on 2018/4/3 AD.
 */
class ServerTest extends GroovyTestCase {
    List<Server> servers = new ArrayList<>();
    List<Client>  clients= new ArrayList<>();
    List<IFile>  files= new ArrayList<>();
    List<Link>  links= new ArrayList<>();
    NetworkGraph networkGraph = NetworkGraph.networkGraph;

    void setUp() {
        super.setUp()
        IFile file1 = new IFile(1,100);
        IFile file2 = new IFile(2,200);
        IFile file3 = new IFile(3,300);
        IFile file4 = new IFile(4,400);
//        IFile file5 = new IFile(5,500);

        List<IFile> fileList1 = new ArrayList<>(1);
        List<IFile> fileList2 = new ArrayList<>(1);
        List<IFile> fileList3 = new ArrayList<>(1);
        List<IFile> fileList4 = new ArrayList<>(1);
//        List<IFile> fileList5 = new ArrayList<>(1);
        fileList1.add(file1);
        fileList2.add(file2);
        fileList3.add(file3);
        fileList4.add(file1);
        fileList4.add(file2);
        fileList4.add(file3);
        fileList4.add(file4);
//        fileList5.add(file5);
        Map<Integer, List<Server>> serversHavingFile = new HashMap<>();
        Server server1 = new Server(1, fileList1,serversHavingFile);
        Server server2 = new Server(2, fileList2,serversHavingFile);
        Server server3 = new Server(3, fileList3,serversHavingFile);
        Server server4 = new Server(4, fileList4,serversHavingFile);
//        Server server5 = new Server(5,fileList5);


        Client client1  = new Client(1);
        Client client2  = new Client(2);

        Link link1 = new Link(client1,server1,2f,1000f,1);
        Link link2 = new Link(client2,server2,2f,1000f,1);
        Link link3 = new Link(server1,server2,2f,1000f,1);
        Link link4 = new Link(server1,server3,2f,1000f,1);
        Link link5 = new Link(server2,server3,2f,1000f,1);
        Link link6 = new Link(server3,server4,2f,1000f,1);

        client1.setLink(link1);
        client2.setLink(link2);

        server1.getLinks().put(server2,link3);
        server1.getLinks().put(server3,link4);
        server1.getLinks().put(client1,link1);


        server2.getLinks().put(server3,link5);
        server2.getLinks().put(server1,link3);
        server2.getLinks().put(client2,link2);


        server3.getLinks().put(server1,link4);
        server3.getLinks().put(server2,link5);
        server3.getLinks().put(server4,link6);

        server4.getLinks().put(server3,link6);

        links.add(link1);
        links.add(link2);
        links.add(link3);
        links.add(link4);
        links.add(link5);
        links.add(link6);

        servers.add(server1);
        servers.add(server2);
        servers.add(server3);
        servers.add(server4);

        clients.add(client1);
        clients.add(client2);

        files.add(file1);
        files.add(file2);
        files.add(file3);
        files.add(file4);

        for (Server s:this.servers){
            networkGraph.addVertex(s);
        }
        for (Client c:this.clients){
            networkGraph.addVertex(c);
        }
        for (Link l:this.links){
            networkGraph.addEdge(l,l.endPointA,l.endPointB, EdgeType.UNDIRECTED);
        }

        StringBuffer sb = new StringBuffer();
        List<Server> serversss ;
        sb.append(" ***** Files ***** ");


        for(IFile f: files){
            serversss = networkGraph.getServersHavingFile(f.getId());
            serversHavingFile.put(f.getId(),serversss);
            sb.append("\n").append(f).append(" :");
            for(Server s: serversss){
                sb.append("  ").append(s);
            }
        }
        Logger.print(sb.toString(),0);
        println()
        networkGraph.buildRoutingTables();

        //topology:
        //                   server4
        //                      |
        //                      |
        //                    link6
        //                      |
        //                      |
        //                   server3
        //                      /\
        //                     /  \
        //                   link4 link5
        //                   /      \
        //                  /        \
        //            server1-link3-server2
        //                  |       |
        //                  |       |
        //                link1   link2
        //                  |       |
        //                  |       |
        //               client1  client2






    }







    void testIsReceivedDataValid() {
        Server server = servers.get(0);
        assertFalse(server.isReceivedDataValid(links.get(1)));
        assertFalse(server.isReceivedDataValid(links.get(4)));
        assertFalse(server.isReceivedDataValid(links.get(5)));
        assertTrue(server.isReceivedDataValid(links.get(0)));
        assertTrue(server.isReceivedDataValid(links.get(2)));
        assertTrue(server.isReceivedDataValid(links.get(3)));
    }

    void testScenario1() {
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(0),1f,null, 4)
        );
        Brain.handleEvents();
        println ("saeedHD");
    }

    void testScenario2() {
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(0),1f,null, 3)
        );
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(1),1.1f,null, 3)
        );
        Brain.handleEvents();
    }


    void testScenario3() {
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(0),1f,null, 3)
        );
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(1),1f,null, 3)
        );
        Brain.handleEvents();
    }

    void testScenario4() {
        files.get(2).size = 100000000 ;
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(0),1f,null, 3)
        );
        EventsQueue.addEvent(
                new Event(EventType.sendReq,clients.get(1),1f,null, 3)
        );
        Brain.handleEvents();
    }



    void testFindFile() {

    }

    void testLink() {
        Segment segment = new Segment(1,clients.get(0),servers.get(3),10,SegmentType.Request, new Request(clients.get(0),servers.get(3),1,1));
        EventsQueue.addEvent(
                new Event(EventType.sendData,links.get(0),1f,clients.get(0), segment)
        );
        EventsQueue.addEvent(
                new Event(EventType.sendData,links.get(0),1.2f,clients.get(0), segment)
        );
        Brain.handleEvents();
    }


    void testSendData() {
        Server server = servers.get(0);
        Link link = server.getLinks().get(servers.get(2));
        Segment segment = new Segment(1,server,servers.get(3),10,SegmentType.Request, new Request(clients.get(0),servers.get(3),1,1));
        server.sendData(0.5f,link,segment);
        Brain.handleEvents();
    }

    void testReceiveData() {

    }

    void testIsThisDeviceDestined() {
        Server server = servers.get(0);
        assertFalse(server.isThisDeviceDestined(new Segment(1,server,servers.get(1),10,SegmentType.Data)));
        assertFalse(server.isThisDeviceDestined(new Segment(2,server,servers.get(2),10,SegmentType.Request)));
        assertFalse(server.isThisDeviceDestined(new Segment(3,servers.get(3),servers.get(2),10,SegmentType.Data)));
        assertTrue(server.isThisDeviceDestined(new Segment(3,servers.get(1),server,10,SegmentType.Data)));
        assertTrue(server.isThisDeviceDestined(new Segment(3,servers.get(2),server,10,SegmentType.Request)));
    }

    void testGetSuitableServer() {
        servers.get(2).queue.add(new Request(clients.get(0),servers.get(2),2,6))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,7))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,8))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,9))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,10))
//        println (server);
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.WMC;
        Server server = servers.get(0).getSuitableServer(new Request(clients.get(0),servers.get(0),3,3));
        assertEquals(server,servers.get(2));
        SimulationParameters.redirectingAlgorithmType = RedirectingAlgorithmType.MCS;
        server = servers.get(0).getSuitableServer(new Request(clients.get(0),servers.get(0),3,3));
        assertEquals(server,servers.get(2));

    }

    void testGetNearestServer() {
        List<Server> list = new ArrayList<>();
        list.add(servers.get(2));
        list.add(servers.get(0));
        list.add(servers.get(1));
        list.add(servers.get(3));
        List<Server> nearests = networkGraph.getNearestServers(2,list,clients.get(0));
        assertTrue(nearests.contains(servers.get(0)));
        assertTrue(nearests.contains(servers.get(2)) | nearests.contains(servers.get(1)));
    }


    void testGetLeastLoadedServer() {

        List<Server> list = new ArrayList<>();
        list.add(servers.get(2));
        list.add(servers.get(3));
        servers.get(2).queue.add(new Request(clients.get(0),servers.get(2),2,6))
        assertEquals(networkGraph.getLeastLoadedServer(list),servers.get(3));
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,7))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,8))
        assertEquals(networkGraph.getLeastLoadedServer(list),servers.get(2));
        servers.get(2).queue.add(new Request(clients.get(0),servers.get(2),2,9))
        servers.get(3).queue.add(new Request(clients.get(0),servers.get(2),2,10))
        assertEquals(networkGraph.getLeastLoadedServer(list),servers.get(2));
        list.add(servers.get(0));
        assertEquals(networkGraph.getLeastLoadedServer(list),servers.get(0));


    }

    void testServeRequest() {
        Request req = new Request(clients.get(0), servers.get(2),3,1);
        Request req2 = new Request(clients.get(0), servers.get(2),4,1);
        req.setRedirect(true);
        req2.setRedirect(true);
        servers.get(2).serveRequest(0f,new Request(clients.get(0), servers.get(2),3,1));
        println ("line")
        servers.get(2).serveRequest(0f,req);
        println ("line")
        servers.get(2).serveRequest(0f,new Request(clients.get(0), servers.get(2),4,1));
        println ("line")

//        servers.get(2).serveRequest(0f,req2);


    }


}

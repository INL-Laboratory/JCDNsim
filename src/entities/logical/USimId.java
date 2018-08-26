package entities.logical;

public class USimId {
    public int ithPoint , jthRun;
    public int id;
    public String simulationName;
    public String timeStamp;



    public USimId(int ithPoint, int jthRun, int id, String simulationName, String timeStamp) {
        this.ithPoint = ithPoint;
        this.jthRun = jthRun;
        this.id = id;
        this.simulationName = simulationName;
        this.timeStamp = timeStamp;
    }
}

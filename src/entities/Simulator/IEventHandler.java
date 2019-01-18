package entities.Simulator;

public abstract class IEventHandler {
    public abstract void handleEvent(Event event) throws Exception;
}

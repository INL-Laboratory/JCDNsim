package entities.logical;

public abstract class IEventHandler {
    public abstract void handleEvent(Event event) throws Exception;
}

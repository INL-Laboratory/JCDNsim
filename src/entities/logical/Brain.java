package entities.logical;

public class Brain {

    EventsQueue eventsQueue;

    public void handleEvents(){
        while (eventsQueue.hasEvent()){
            Event event = eventsQueue.popEvent();
            try {
                event.getRelatedEntity().handleEvent(event);

            }
            catch (Exception e){
                System.err.println("Exception Occurred : ");
                e.printStackTrace();
                return;
            }
        }
    }

    public EventsQueue getEventsQueue() {
        return eventsQueue;
    }

    public void setEventsQueue(EventsQueue eventsQueue) {
        this.eventsQueue = eventsQueue;
    }
}

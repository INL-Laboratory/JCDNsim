package entities.logical;

public class Brain {

//    EventsQueue eventsQueue;

    public static void handleEvents(){
        while (EventsQueue.hasEvent()){
            Event event = EventsQueue.popEvent();
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

    public static void main(String[] args) {

    }


}

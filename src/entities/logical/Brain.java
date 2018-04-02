package entities.logical;

import entities.utilities.logger.Logger;

public class Brain {

//    EventsQueue eventsQueue;

    public static void handleEvents(){
        while (EventsQueue.hasEvent()){
            Event event = EventsQueue.popEvent();
            try {
                Logger.ePrint("Event is Handling:" + event.toString(),event.getTime());
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

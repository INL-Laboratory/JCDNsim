package entities.logical;

import entities.physical.Server;
import entities.utilities.logger.Logger;

public class Brain {

//    EventsQueue eventsQueue;
    public static double totalTimeInBrain=0;
    public static void handleEvents(){
        while (EventsQueue.hasEvent()){
            Event event = EventsQueue.popEvent();
            try {
//                Logger.ePrint("Event is being handled:" + event.toString(),event.getTime());
//                if (event.getType() == EventType.requestServed && ((Server)event.getRelatedEntity()).getServerLoad()>500){
//                    System.out.println();
//                }
                double tempTime = System.currentTimeMillis();
                event.getRelatedEntity().handleEvent(event);
                totalTimeInBrain+= System.currentTimeMillis() - tempTime;

            }
            catch (OkayException e){
                Logger.print(e.getMessage(),e.getTime());
//                return;                   Why was this put here? Exceptions are defined by ourselves and after catching one the next event should be handled.
            }catch (Exception e){
//                System.err.print(" Exception Occurred and program stopped running : ");
                e.printStackTrace();
                return;
            }
        }

    }

    public static void main(String[] args) {

    }


}

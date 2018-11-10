package entities.logical;


public class Brain {
    public EventsQueue eventsQueue;

    /**
     * Handle events crawls on the queue of events and performs the action stated in each, one by one, whatever it is.
     * This is the place that exceptions are handled.
     */
//    EventsQueue eventsQueue;
//    public static double totalTimeInBrain=0;
    public void handleEvents(){
//            double tempTime = System.currentTimeMillis();
        while (eventsQueue.hasEvent()){
            Event event = eventsQueue.popEvent();
            try {
//                Logger.ePrint("Event is being handled:" + event.toString(),event.getTime());
//                if (event.getType() == EventType.requestServed && ((Server)event.getRelatedEntity()).getServerLoad()>500){
//                    System.out.println();
//                }

                event.getRelatedEntity().handleEvent(event);

            }
            catch (OkayException e){
//                Logger.print(e.getMessage(),e.getTime());
//                return;                   Why was this put here? Exceptions are defined by ourselves and after catching one the next event should be handled.
            }catch (Exception e){
//                System.err.print(" Exception Occurred and program stopped running : ");
                e.printStackTrace();
                return;
            }
        }
//                totalTimeInBrain+= System.currentTimeMillis() - tempTime;

    }


}

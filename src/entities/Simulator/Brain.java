/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;


public class Brain {
    public EventsQueue eventsQueue;

    /**
     * Handle events pops the events from the queue of events and performs the action stated in each, one by one, whatever it is.
     * This is the place that exceptions are handled.
     */
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

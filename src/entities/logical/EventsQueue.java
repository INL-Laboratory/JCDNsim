package entities.logical;

import entities.physical.ProjectRun;
import entities.utilities.BinaryHeap;

public class EventsQueue {
    private static BinaryHeap<Event> queue = new BinaryHeap<>(false);
    public static float lastSentPeriod = 0;
    public static void addEvent(Event event){
//        if(queue.() == 0){
//            queue.add(event);
//            return;
//        }
//
//        for(int i=0; i<queue.size(); i++){
//            if(queue.get(i).getTime() > event.getTime()) {
//                queue.add(i, event);
//                return;
//            }
//        }
//        queue.add(event);

        queue.add(event);
    }
    public static float maximumTime = 0;
    public static Event popEvent() {
//        if(queue.size() == 0){
//            return null;
//        }
//        return queue.remove(0);
//        System.out.println( queue);
        Event event = queue.peek();
        boolean isPeriodic = UpdateType.periodic == SimulationParameters.updateType;
        boolean isPiggyGroupedPeriodic = UpdateType.piggyGroupedPeriodic == SimulationParameters.updateType;
        if ( isPeriodic || isPiggyGroupedPeriodic )
            if (event.getOptionalData() instanceof Segment && ((Segment) event.getOptionalData()).getSegmentType() != SegmentType.Update)
                if (Float.compare(event.getTime(), lastSentPeriod) == 1) {
                    ProjectRun.sendPeriodicUpdate(lastSentPeriod, isPiggyGroupedPeriodic);
                    lastSentPeriod += DefaultValues.periodicStep;

        }






        event = queue.remove();
        if (event.getTime()>maximumTime) maximumTime= event.getTime();


        return event;
    }
    public static boolean hasEvent(){
        return queue.length() > 0;
    }

}

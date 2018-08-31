package entities.logical;

import entities.utilities.BinaryHeap;

public class EventsQueue {
    private BinaryHeap<Event> queue = new BinaryHeap<>(false);
    public float lastSentPeriod = 0;
    UnitSimulation unitSimulation;
    public void addEvent(Event event){
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
    public float maximumTime = 0;
    public  Event popEvent() {
//        if(queue.size() == 0){
//            return null;
//        }
//        return queue.remove(0);
//        System.out.println( queue);
        Event event = queue.peek();
        boolean isPeriodic = UpdateType.periodic == unitSimulation.algorithmData.updateType;
        boolean isPiggyGroupedPeriodic = UpdateType.piggyGroupedPeriodic == unitSimulation.algorithmData.updateType;
        if ( isPeriodic || isPiggyGroupedPeriodic )
            if (event.getOptionalData() instanceof Segment && ((Segment) event.getOptionalData()).getSegmentType() != SegmentType.Update)
                if (Float.compare(event.getTime(), lastSentPeriod) == 1) {
                    unitSimulation.sendPeriodicUpdate(lastSentPeriod, isPiggyGroupedPeriodic);
                    //TODO that guy's algorithm
                    if (unitSimulation.algorithmData.redirectingAlgorithmType==RedirectingAlgorithmType.CostBased)
                        unitSimulation.setShares();
                    lastSentPeriod += unitSimulation.algorithmData.periodicStep;

        }






        event = queue.remove();
//        if (event.getTime()>maximumTime) maximumTime= event.getTime();


        return event;
    }
    public boolean hasEvent(){
        return queue.length() > 0;
    }

    public EventsQueue(UnitSimulation unitSimulation) {
        this.unitSimulation = unitSimulation;
    }
}

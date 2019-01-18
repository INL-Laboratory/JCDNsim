/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

import entities.Network.Segment;
import entities.Setting.RedirectingAlgorithmType;
import entities.Setting.SegmentType;
import entities.Setting.UpdateType;
import entities.Utilities.BinaryHeap;



public class EventsQueue {
    private BinaryHeap<Event> queue = new BinaryHeap<>(false);
    public float lastSentPeriod = 0;
    UnitSimulation unitSimulation;


    /**
     * Add a newly created event to the event's queue
     * @param event the event to be added
     */
    public void addEvent(Event event){
        queue.add(event);
    }


    /**
     * @return pops and event from the event's queue. For the periodic algorithm and its counterpart, it takes care of sending the update packages on due times. Other
     */
    public  Event popEvent() {
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
                    lastSentPeriod += unitSimulation.algorithmData.periodicStep.floatValue();

        }



        event = queue.remove();


        return event;
    }
    public boolean hasEvent(){
        return queue.length() > 0;
    }

    public EventsQueue(UnitSimulation unitSimulation) {
        this.unitSimulation = unitSimulation;
    }
}

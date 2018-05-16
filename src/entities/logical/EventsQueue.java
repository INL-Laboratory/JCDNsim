package entities.logical;

import entities.utilities.BinaryHeap;

import java.util.ArrayList;
import java.util.List;

public class EventsQueue {
    private static BinaryHeap<Event> queue = new BinaryHeap<>(false);

    public static void renewQueue(){queue=new BinaryHeap<>(false);}
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

    public static Event popEvent(){
//        if(queue.size() == 0){
//            return null;
//        }
//        return queue.remove(0);
//        System.out.println( queue);

        return queue.remove();
    }
    public static boolean hasEvent(){
        return queue.length() > 0;
    }

}

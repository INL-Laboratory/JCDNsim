package entities.logical;

import java.util.ArrayList;
import java.util.List;

public class EventsQueue {
    private static List<Event> queue = new ArrayList<>();

    public void addEvent(Event event){
        if(queue.size() == 0){
            queue.add(event);
            return;
        }

        for(int i=0; i<queue.size(); i++){
            if(queue.get(i).getTime() > event.getTime()) {
                queue.add(i, event);
                return;
            }
        }
        queue.add(event);
    }

    public Event popEvent(){
        if(queue.size() == 0){
            return null;
        }
        return queue.remove(0);
    }

    public boolean hasEvent(){
        return queue.size() > 0;
    }

}

package entities.logical;

import entities.utilities.logger.Logger;

public class Event <T extends IEventHandler> implements Comparable {
    private EventType type;
    private T relatedEntity;
    private float time;
    private T creator;
    private Object optionalData;

    public Event(EventType eventType, T relatedEntity, float time, T creator){
        this.type = eventType;
        this.relatedEntity = relatedEntity;
        this.time = time;
        this.creator = creator;
        Logger.ePrint("Event created: " + this, 0f);
    }

    public Event(EventType eventType, T relatedEntity, float time, T creator, Object optionalData){
        this(eventType,relatedEntity,time,creator);
        this.optionalData = optionalData;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public T getRelatedEntity() {
        return relatedEntity;
    }

    public void setRelatedEntity(T relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public T getCreator() {
        return creator;
    }

    public void setCreator(T creator) {
        this.creator = creator;
    }

    public Object getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(Object optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Event{");
        sb.append("type=").append(type);
        sb.append(", relatedEntity=").append(relatedEntity);
        sb.append(", time=").append(time);
        sb.append(", creator=").append(creator);
        sb.append(", optionalData=").append(optionalData);
        sb.append('}');
        return sb.toString();
    }



    @Override
    public int compareTo(Object o) {
        return Float.compare(((Event)o).time,this.time);
    }
}

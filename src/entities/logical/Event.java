package entities.logical;

public class Event <T extends IEventHandler> {
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
    }

    public Event(EventType eventType, T relatedEntity, float time, T creator, Object optionalData){
        this.type = eventType;
        this.relatedEntity = relatedEntity;
        this.time = time;
        this.creator = creator;
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
}

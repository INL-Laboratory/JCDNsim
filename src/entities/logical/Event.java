package entities.logical;

public class Event <T extends IEventHandler> {
    private EventType type;
    private T relatedEntity;
    private float time;

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
}

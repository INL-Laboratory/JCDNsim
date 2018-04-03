package entities.logical;

/**
 * Created by hd on 2018/4/4 AD.
 */
public class OkayException extends Exception {
    private float time;
    public OkayException(String message, float time) {
        super(message);
        this.time = time;
    }

    public float getTime() {
        return time;
    }
}

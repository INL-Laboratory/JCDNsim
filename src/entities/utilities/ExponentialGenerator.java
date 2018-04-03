package entities.utilities;

public class ExponentialGenerator extends Generator {

    //When making a new event with type sendReq please put the id of Requested file in the optional content of the event
    public ExponentialGenerator(float lambda) {
        super(lambda);
    }

    @Override
    public float generate() {
        return (float) ((-1 * Math.log(1.0 - Math.random()))/lambda);
    }
}

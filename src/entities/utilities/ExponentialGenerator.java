package entities.utilities;

public class ExponentialGenerator extends Generator {


    public ExponentialGenerator(float lambda) {
        super(lambda);
    }

    @Override
    public float generate() {
        return (float) ((-1 * Math.log(1.0 - Math.random()))/lambda);
    }
}

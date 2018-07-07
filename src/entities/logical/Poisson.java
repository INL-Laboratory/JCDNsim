package entities.logical;

import java.util.Random;

public class Poisson {
    private double lambda;
    private Random random;
    public float getNextTime(){
        return (float)(-Math.log(1.0- random.nextDouble())/lambda);
    }

    public Poisson(double lambda , Random random) {
        this.lambda = lambda;
        this.random = random;
    }
}

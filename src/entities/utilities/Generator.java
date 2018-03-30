package entities.utilities;

public abstract class Generator {

    float lambda;

    Generator(float lambda){
        this.lambda = lambda;
    }

    public abstract float generate();


}


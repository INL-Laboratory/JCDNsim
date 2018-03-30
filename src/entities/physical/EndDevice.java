package entities.physical;

import entities.logical.IEventHandler;

public abstract class EndDevice extends IEventHandler{
    /**
     * Instead of using IP addresses, by this number we specify the servers and clients.
     */
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

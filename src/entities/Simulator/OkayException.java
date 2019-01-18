/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

/**
 * Created by hd on 2018/4/4 AD.
 * Exceptions that are not intended to halt the program, just to inform that sth in the network happened: a package was lost or etc.
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

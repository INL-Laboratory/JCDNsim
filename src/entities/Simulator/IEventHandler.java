/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Simulator;

public abstract class IEventHandler {
    public abstract void handleEvent(Event event) throws Exception;
}

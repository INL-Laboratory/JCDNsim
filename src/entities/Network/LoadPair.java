/*
 * Developed By Saeed Hadadan, INL lab, Sharif University of Technology: www.inl-lab.net
 * Copyright (c) 2019. All rights reserved.
 *
 */

package entities.Network;

public class LoadPair {
    Server server;
    int load;

    public LoadPair(Server server, int load) {
        this.server = server;
        this.load = load;
    }
}
